var userLocale = null;

const DISPLAY_FORMAT_DATE = 'DD MMMM YYYY';
const SERVER_FORMAT_DATE = 'YYYY-MM-DD';

const PT_EN_MONTHS_MAPPING = {
  'Janeiro': 'January',
  'Fevereiro': 'February',
  'Março': 'March',
  'Abril': 'April',
  'Maio': 'May',
  'Junho': 'June',
  'Julho': 'July',
  'Agosto': 'August',
  'Setembro': 'September',
  'Outubro': 'October',
  'Novembro': 'November',
  'Dezembro': 'December'
};

jq(document).ready(function () {
  emr.loadMessages([
    "visits.genericSuccess",
    "visits.genericFailure",
    "visitSavedOnText",
    "precedingVisitPlannedOnText",
    "duplicatedVisitDateText",
    "daysBeforeText",
    "whileText",
    "nextVisitPlannedOnText",
    "daysAfterVisitText",
    "cfl.weekDay.Sunday.fullName",
    "cfl.weekDay.Monday.fullName",
    "cfl.weekDay.Tuesday.fullName",
    "cfl.weekDay.Wednesday.fullName",
    "cfl.weekDay.Thursday.fullName",
    "cfl.weekDay.Friday.fullName",
    "cfl.weekDay.Saturday.fullName",
    "visits.outsideDateWindowInfoMessage"
  ]);

  userLocale = jq('#locale-span').text();

  jq('#visit-date-select').datepicker({
    format: 'dd MM yyyy',
    forceParse: false,
    autoclose: true,
    language: userLocale
  });
});

let editVisit = window.editVisit || {};

editVisit.editVisitDialog = null;
editVisit.extraInfoDialog = null;

/**
 * Close HackWrap div and cleanup any changes.
 */
editVisit.hackWrapClose = function () {
  jq('#simplemodal-container').detach().appendTo('body');
  jq('#hackWrap').remove();
  jq('body').css({'overflow': 'auto'});
  editVisit.editVisitDialog.close();
};

editVisit.showEditVisitDialog = function (isExtraInfoDialogEnabled, isOutsideDateWindowInformationEnabled, holidayWeekdays,
                                          allVisitDates, visitUuid, patientUuid, visitDate, visitTime, visitLocation,
                                          visitType, visitStatus, isVisitHasEncounters, lowWindowDate, upWindowDate) {

  editVisit.createEditVisitDialog(isExtraInfoDialogEnabled, isOutsideDateWindowInformationEnabled, holidayWeekdays,
                                  allVisitDates, visitUuid, patientUuid, visitDate, visitStatus, lowWindowDate, upWindowDate);

  editVisit.editVisitDialog.show();

  // Create custom scrollable overlay hackWrap div for https://www.ericmmartin.com/projects/simplemodal/
  let hackWrap = document.createElement("div");
  jq(hackWrap).attr('id', 'hackWrap').click(function (e) {
    editVisit.hackWrapClose();
  }).css({
    position: 'fixed',
    top: 0,
    right: 0,
    bottom: 0,
    left: 0,
    width: '100%',
    height: '100%',
    overflow: 'auto',
    'z-index': 1004,
    display: 'flex'
  });

  // Prevent scrollable body and hackWrap at teh same time
  jq('body').append(hackWrap).css({'overflow': 'hidden'});

  // Change styles of original overlay and modal container and ensure they stay changed during window resize
  let customContainerCSSOverride = {
    position: 'relative',
    height: '100%',
    width: '100%',
    left: 0,
    top: 0,
    'z-index': 1003
  };
  jq('#simplemodal-container').detach().appendTo(hackWrap).css(customContainerCSSOverride);
  let customOverlayCSSOverride = {height: 0, width: 0};
  jq('#modal-overlay').css(customOverlayCSSOverride);
  $(window).on('resize', function () {
    jq('#modal-overlay').css(customOverlayCSSOverride);
    jq('#simplemodal-container').css(customContainerCSSOverride);
  });

  // Prevent propagation of click events to hackWrap overlay when clicked over a dialog div
  jq('#edit-visit-dialog').click(function (e) {
    e.stopPropagation();
  });

  setTimeout(() => {
    jq('.datepicker').hide();
    jq('#visit-date-select').blur();
  }, 50);

  jq('#visit-date-select').datepicker('setDate', new Date(visitDate));
  jq('#visit-date-select').val(moment(visitDate).locale(userLocale).format(DISPLAY_FORMAT_DATE));
  jq('#visit-time-select').val(visitTime);
  jq('#visit-location-select').val(visitLocation);
  jq('#visit-type-select').val(visitType);
  jq('#visit-status-select').val(visitStatus);

  if (isVisitHasEncounters == 'true') {
    jq('#visit-type-select').attr('disabled', true);
  } else {
    jq('#visit-type-select').attr('disabled', false);
  }

  jq('.error-label').hide();
  enableSaveButton();
};

editVisit.createEditVisitDialog = function (isExtraInfoDialogEnabled, isOutsideDateWindowInformationEnabled,
                                            holidayWeekdays, allVisitDates, visitUuid, patientUuid, visitDate,
                                            visitStatus, lowWindowDate, upWindowDate) {

  const lowWindowDateAsDateObject = new Date(lowWindowDate);
  lowWindowDateAsDateObject.setHours(0, 0, 0, 0);
  const upWindowDateAsDateObject = new Date(upWindowDate);
  upWindowDateAsDateObject.setHours(0, 0, 0, 0);

  editVisit.editVisitDialog = emr.setupConfirmationDialog({
    selector: '#edit-visit-dialog',
    dialogOpts: {
      onClose: function () {
        editVisit.hackWrapClose();
      }
    },
    actions: {
      confirm: function () {
        const currentVisitDate = new Date(getCurrentDateInServerFormat());
        currentVisitDate.setHours(0, 0, 0, 0);
        const isNewVisitDateInRange = currentVisitDate >= lowWindowDateAsDateObject && currentVisitDate <= upWindowDateAsDateObject;
        const dateWindowInfoAvailable = lowWindowDate != 'null' && upWindowDate != 'null';
        const shouldOutsideDateWindowInformationBeDisplayed = isOutsideDateWindowInformationEnabled && visitStatus === 'SCHEDULED'
          && !isNewVisitDateInRange && dateWindowInfoAvailable;

        if (isExtraInfoDialogEnabled || shouldOutsideDateWindowInformationBeDisplayed) {
          editVisit.showExtraInfoDialog(isExtraInfoDialogEnabled, shouldOutsideDateWindowInformationBeDisplayed, allVisitDates, holidayWeekdays, visitUuid, patientUuid, visitDate);
        } else {
          const requestBody = {
            "uuid": visitUuid,
            "startDate": getCurrentDateInServerFormat(),
            "location": jq('#visit-location-select').val(),
            "time": jq('#visit-time-select').val(),
            "type": jq('#visit-type-select').val(),
            "status": jq('#visit-status-select').val(),
            "patientUuid": patientUuid
          };
          jq.ajax({
            url: `/${OPENMRS_CONTEXT_PATH}/ws/visits/${visitUuid}`,
            type: 'PUT',
            data: JSON.stringify(requestBody),
            headers: {
              'Content-type': 'application/json; charset=utf-8'
            },
            success: function () {
              emr.successMessage("visits.genericSuccess");
              editVisit.editVisitDialog.close();
              location.reload();
            },
            error: function () {
              emr.errorMessage("visits.genericFailure");
              editVisit.editVisitDialog.close();
            }
          });
        }
      },
      cancel: function () {
        editVisit.editVisitDialog.close();
      }
    }
  })
};

editVisit.showExtraInfoDialog = function (isExtraInfoDialogEnabled, shouldOutsideDateWindowInformationBeDisplayed,
                                          allVisitDates, holidayWeekdays, visitUuid, patientUuid, visitDate) {
  editVisit.createExtraInfoDialog(visitUuid, patientUuid);

  jq('#extra-info-dialog').show();
  jq('#infoMessagePart1').text('');
  jq('#infoMessagePart2').text('');
  jq('#infoMessagePart1').css('color', '#333333');
  jq('#outsideDateWindowInfo').text('');
  setExtraInfoDialogContent(isExtraInfoDialogEnabled, shouldOutsideDateWindowInformationBeDisplayed, allVisitDates, holidayWeekdays, visitDate);
}

editVisit.createExtraInfoDialog = function (visitUuid, patientUuid) {
  editVisit.extraInfoDialog = emr.setupConfirmationDialog({
    selector: '#extra-info-dialog',
    actions: {
      confirm: function () {
        const requestBody = {
          "uuid": visitUuid,
          "startDate": getCurrentDateInServerFormat(),
          "location": jq('#visit-location-select').val(),
          "time": jq('#visit-time-select').val(),
          "type": jq('#visit-type-select').val(),
          "status": jq('#visit-status-select').val(),
          "patientUuid": patientUuid
        };
        jq.ajax({
          url: `/${OPENMRS_CONTEXT_PATH}/ws/visits/${visitUuid}`,
          type: 'PUT',
          data: JSON.stringify(requestBody),
          headers: {
            'Content-type': 'application/json; charset=utf-8'
          },
          success: function () {
            emr.successMessage("visits.genericSuccess");
            jq('#extra-info-dialog').hide();
            location.reload();
          },
          error: function () {
            emr.errorMessage("visits.genericFailure");
            jq('#extra-info-dialog').hide();
          }
        });
      },
      cancel: function () {
        jq('#extra-info-dialog').hide();
      }
    }
  })
};

editVisit.handleDateInputOnClick = function () {
  jq('.datepicker').show();
}

function getCurrentDateInServerFormat () {
  let stringVisitDate = jq('#visit-date-select').val();

  if (userLocale.includes('pt')) {
    for (const [ptMonth, enMonth] of Object.entries(PT_EN_MONTHS_MAPPING)) {
      stringVisitDate = stringVisitDate.replace(ptMonth, enMonth);
    }
  }
  const currentDateInputValue = moment(stringVisitDate, DISPLAY_FORMAT_DATE);

  return moment(currentDateInputValue).format(SERVER_FORMAT_DATE);
}

function setExtraInfoDialogContent(isExtraInfoDialogEnabled, shouldOutsideDateWindowInformationBeDisplayed,
                                    allVisitDates, holidayWeekdays, visitDate) {

  if (isExtraInfoDialogEnabled) {
    const allVisitDatesArray = allVisitDates.split(',');
    allVisitDatesArray.splice(allVisitDatesArray.indexOf(visitDate), 1);

    const newVisitDate = moment(jq('#visit-date-select').datepicker('getDate')).format(SERVER_FORMAT_DATE);
    const isVisitDateDuplicated = allVisitDatesArray.includes(newVisitDate);
    if (isVisitDateDuplicated) {
      allVisitDatesArray.splice(allVisitDatesArray.indexOf(newVisitDate), 1);
    }

    let stringVisitDate = jq('#visit-date-select').val();
    if (userLocale.includes('pt')) {
      for (const [ptMonth, enMonth] of Object.entries(PT_EN_MONTHS_MAPPING)) {
        stringVisitDate = stringVisitDate.replace(ptMonth, enMonth);
      }
    }

    const currentVisitDate = moment(stringVisitDate, DISPLAY_FORMAT_DATE);
    const currentVisitWeekDayName = moment(currentVisitDate).format('dddd');
    const weekDayNameToDisplay = emr.message('cfl.weekDay.' + currentVisitWeekDayName + '.fullName');

    const currentVisitDateAsDateObject = new Date(currentVisitDate);
    const allVisitDateObjects = allVisitDatesArray.map(date => new Date(date));
    const closestPreviousVisit = findClosestPreviousVisit(allVisitDateObjects, currentVisitDateAsDateObject);
    const closestFutureVisit = findClosestFutureVisit(allVisitDateObjects, currentVisitDateAsDateObject);

    jq('#infoMessagePart1')
      .append(emr.message('visitSavedOnText'))
      .append(' ')
      .append(weekDayNameToDisplay)
      .append(', ')
      .append(jq('#visit-date-select').val())
      .append('.');

    if (isVisitDateDuplicated) {
      jq('#infoMessagePart1')
        .append('</br>')
        .append('<span>')
        .append(emr.message('duplicatedVisitDateText'))
        .append('</span>');
    }

    if (closestPreviousVisit != null) {
      const numberOfDaysBetweenCurrentAndPastVisit = getNumberOfDaysBetweenDates(currentVisitDateAsDateObject, closestPreviousVisit);
      jq('#infoMessagePart2')
        .append(emr.message('precedingVisitPlannedOnText'))
        .append(' ')
        .append(numberOfDaysBetweenCurrentAndPastVisit)
        .append(' ')
        .append(emr.message('daysBeforeText'));

      if (closestFutureVisit == null) {
        jq('#infoMessagePart2').append('.');
      }
    }

    if (closestPreviousVisit != null && closestFutureVisit != null) {
      jq('#infoMessagePart2')
        .append(', ')
        .append(emr.message('whileText'))
        .append(' ');
    }

    if (closestFutureVisit != null) {
      const numberOfDaysBetweenCurrentAndFutureVisit = getNumberOfDaysBetweenDates(currentVisitDateAsDateObject, closestFutureVisit);
      jq('#infoMessagePart2')
        .append(emr.message('nextVisitPlannedOnText'))
        .append(' ')
        .append(numberOfDaysBetweenCurrentAndFutureVisit)
        .append(' ')
        .append(emr.message('daysAfterVisitText'))
        .append('.');

      if (closestPreviousVisit == null) {
        const infoMessageText = jq('#infoMessagePart2').text();
        jq('#infoMessagePart2').html(infoMessageText.charAt(0).toUpperCase() + infoMessageText.slice(1));
      }
    }

    const holidayWeekdaysArray = holidayWeekdays.split(',');
    if (holidayWeekdaysArray.includes(currentVisitWeekDayName)) {
      jq('#infoMessagePart1').css('color', 'red');
    }
  } else {
    jq('#extraInfoDiv').hide();
  }

  if (shouldOutsideDateWindowInformationBeDisplayed) {
    jq('#outsideDateWindowInfo').append(emr.message('visits.outsideDateWindowInfoMessage'));
  } else {
    jq('#outsideDateWindowDiv').hide();
  }
}

function getNumberOfDaysBetweenDates (date1, date2) {
  if (date1 && date2) {
    return Math.abs((Date.UTC(date2.getFullYear(), date2.getMonth(), date2.getDate()) -
      Date.UTC(date1.getFullYear(), date1.getMonth(), date1.getDate()))) / (24 * 60 * 60 * 1000);
  }
  return null;
}

function findClosestPreviousVisit (allVisitDates, currentVisitDate) {
  const previousVisitDates = allVisitDates.filter(date => date < currentVisitDate);
  if (!previousVisitDates.length) {
    return null;
  }

  return new Date(Math.max.apply(null, previousVisitDates));
}

function findClosestFutureVisit (allVisitDates, currentVisitDate) {
  const futureVisitDates = allVisitDates.filter(date => date > currentVisitDate);
  if (!futureVisitDates.length) {
    return null;
  }

  return new Date(Math.min.apply(null, futureVisitDates));
}

function onSelectChange (event) {
  const selectedValue = event.value;
  const elementId = event.id;
  if (!selectedValue) {
    jq('#' + elementId + '-error-label').show();
    jq('#save-button').attr('disabled', true);
    jq('#save-button').addClass('disabled-button');
  } else {
    if (isEditVisitFormValid()) {
      enableSaveButton();
    }
  }
}

function isEditVisitFormValid () {
  const requiredSelectFieldIDs = [
    "visit-location-select",
    "visit-type-select",
    "visit-status-select"
  ];

  let isValidForm = true;
  requiredSelectFieldIDs.forEach(fieldID => {
    const value = jq('#' + fieldID).val();
    if (!value) {
      isValidForm = false;
    } else {
      jq('#' + fieldID + '-error-label').hide();
    }
  });

  return isValidForm;
}

function enableSaveButton () {
  jq('#save-button').removeClass('disabled-button');
  jq('#save-button').attr('disabled', false);
}
