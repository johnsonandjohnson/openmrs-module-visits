var userLocale = null;

const FULL_DISPLAY_FORMAT_DATE = 'DD MMMM YYYY';
const SHORT_DISPLAY_FORMAT_DATE = 'DD MMM YYYY';
const SERVER_FORMAT_DATE = 'YYYY-MM-DD';
const CLINIC_CLOSED_WEEKDAYS_ATTRIBUTE_TYPE_UUID = '570e9b8f-752b-4577-9ffb-721e073387d9';
const CLINIC_CLOSED_DATES_ATTRIBUTE_TYPE_UUID = '64b73c1c-c91a-403f-bb26-dd62dc91bfef';

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
    "visits.outsideDateWindowInfoMessage",
    "visits.closedClinicInfoMessage"
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

editVisit.showEditVisitDialog = function(visitConfig, generalConfig) {
  editVisit.createEditVisitDialog(visitConfig, generalConfig);
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

  jq('#visit-date-select').datepicker('setDate', new Date(visitConfig.visitDateInServerFormat));
  jq('#visit-date-select').val(moment(visitConfig.visitDateInServerFormat).locale(userLocale).format(FULL_DISPLAY_FORMAT_DATE));
  jq('#visit-time-select').val(visitConfig.visitDetails.time);
  jq('#visit-location-select').val(visitConfig.visitLocationUuid);
  jq('#visit-type-select').val(visitConfig.visitTypeUuid);
  jq('#visit-status-select').val(visitConfig.visitDetails.status);

  if (visitConfig.isVisitHasEncounters == 'true') {
    jq('#visit-type-select').attr('disabled', true);
  } else {
    jq('#visit-type-select').attr('disabled', false);
  }

  jq('.error-label').hide();
  enableSaveButton();
};

editVisit.createEditVisitDialog = function(visitConfig, generalConfig) {
  const lowWindowDateAsDateObject = new Date(visitConfig.lowWindowDate);
  lowWindowDateAsDateObject.setHours(0, 0, 0, 0);
  const upWindowDateAsDateObject = new Date(visitConfig.upWindowDate);
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
        const currentVisitDate = new Date(getSelectedVisitDate(SERVER_FORMAT_DATE));
        currentVisitDate.setHours(0, 0, 0, 0);
        const isNewVisitDateInRange = currentVisitDate >= lowWindowDateAsDateObject && currentVisitDate <= upWindowDateAsDateObject;
        const dateWindowInfoAvailable = visitConfig.lowWindowDate != 'null' && visitConfig.upWindowDate != 'null';

        const selectedVisitTypeUuid = jq('#visit-type-select').val();
        const isVisitTypeHasTimeWindow = generalConfig.visitTypeUuidsWithTimeWindow.includes(selectedVisitTypeUuid);

        const shouldOutsideDateWindowInformationBeDisplayed = generalConfig.isOutsideDateWindowInformationEnabled && visitConfig.visitDetails.status === 'SCHEDULED'
          && !isNewVisitDateInRange && dateWindowInfoAvailable && isVisitTypeHasTimeWindow;

        if (generalConfig.isExtraInfoDialogEnabled || shouldOutsideDateWindowInformationBeDisplayed) {
          editVisit.showExtraInfoDialog(visitConfig, generalConfig, shouldOutsideDateWindowInformationBeDisplayed);
        } else {
          const requestBody = {
            "uuid": visitConfig.visitUuid,
            "startDate": getSelectedVisitDate(SERVER_FORMAT_DATE),
            "location": jq('#visit-location-select').val(),
            "time": jq('#visit-time-select').val(),
            "type": jq('#visit-type-select').val(),
            "status": jq('#visit-status-select').val(),
            "patientUuid": generalConfig.patientUuid
          };
          jq.ajax({
            url: `/${OPENMRS_CONTEXT_PATH}/ws/visits/${visitConfig.visitUuid}`,
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

editVisit.showExtraInfoDialog = function(visitConfig, generalConfig, shouldOutsideDateWindowInformationBeDisplayed) {
  editVisit.createExtraInfoDialog(visitConfig.visitUuid, generalConfig.patientUuid);

  jq('#extra-info-dialog').show();
  jq('#infoMessagePart1').text('');
  jq('#infoMessagePart2').text('');
  jq('#outsideDateWindowInfo').text('');
  jq('#clinicClosedInfo').text('');
  setExtraInfoDialogContent(visitConfig, generalConfig, shouldOutsideDateWindowInformationBeDisplayed);
}

editVisit.createExtraInfoDialog = function(visitUuid, patientUuid) {
  editVisit.extraInfoDialog = emr.setupConfirmationDialog({
    selector: '#extra-info-dialog',
    actions: {
      confirm: function() {
        const requestBody = {
          "uuid": visitUuid,
          "startDate": getSelectedVisitDate(SERVER_FORMAT_DATE),
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
          success: function() {
            emr.successMessage("visits.genericSuccess");
            jq('#extra-info-dialog').hide();
            location.reload();
          },
          error: function() {
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

editVisit.handleDateInputOnClick = function() {
  jq('.datepicker').show();
}

function getSelectedVisitDate(targetFormat) {
  let stringVisitDate = jq('#visit-date-select').val();

  if (userLocale.includes('pt')) {
    for (const [ptMonth, enMonth] of Object.entries(PT_EN_MONTHS_MAPPING)) {
      stringVisitDate = stringVisitDate.replace(ptMonth, enMonth);
    }
  }
  const currentDateInputValue = moment(stringVisitDate, FULL_DISPLAY_FORMAT_DATE);

  return moment(currentDateInputValue).format(targetFormat);
}

function setExtraInfoDialogContent(visitConfig, generalConfig, shouldOutsideDateWindowInformationBeDisplayed) {
  if (generalConfig.isExtraInfoDialogEnabled) {
    const allVisitDatesArray = [...generalConfig.allVisitDates];
    allVisitDatesArray.splice(allVisitDatesArray.indexOf(visitConfig.visitDateInServerFormat), 1);

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

    const currentVisitDate = moment(stringVisitDate, FULL_DISPLAY_FORMAT_DATE);
    const currentVisitWeekDayName = moment(currentVisitDate).format('dddd');
    const weekDayNameToDisplay = emr.message('cfl.weekDay.' + currentVisitWeekDayName + '.fullName');

    const currentVisitDateAsDateObject = new Date(currentVisitDate);
    const allVisitDateObjects = allVisitDatesArray.map(date => new Date(date));
    const closestPreviousVisit = findClosestPreviousVisit(allVisitDateObjects, currentVisitDateAsDateObject);
    const closestFutureVisit = findClosestFutureVisit(allVisitDateObjects, currentVisitDateAsDateObject);

    const closedClinicWeekdays = generalConfig.locationAttributeDTOs.find((locationDTO) =>
      locationDTO.locationUuid === visitConfig.visitLocationUuid)?.locationAttributesMap?.[CLINIC_CLOSED_WEEKDAYS_ATTRIBUTE_TYPE_UUID];
    const currentVisitWeekday = new Date(getSelectedVisitDate(SERVER_FORMAT_DATE)).toLocaleDateString("en-us", { weekday: "long" });
    const isClosedClinicWeekday = closedClinicWeekdays?.split(",").includes(currentVisitWeekday);

    const closedClinicDates = generalConfig.locationAttributeDTOs.find((locationDTO) =>
      locationDTO.locationUuid === visitConfig.visitLocationUuid)?.locationAttributesMap?.[CLINIC_CLOSED_DATES_ATTRIBUTE_TYPE_UUID];
    const selectedDateInShortDisplayFormat = getSelectedVisitDate(SHORT_DISPLAY_FORMAT_DATE);
    const isClosedClinicDate = closedClinicDates?.split(",").includes(selectedDateInShortDisplayFormat);

    const isClosedClinic = isClosedClinicWeekday || isClosedClinicDate;

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

    if (isClosedClinic) {
      jq('#infoMessagePart1').css('color', 'red');
      jq('#clinicClosedDiv').show();
      jq('#clinicClosedInfo').append(emr.message('visits.closedClinicInfoMessage'));
    } else {
      jq('#infoMessagePart1').css('color', '#333333');
      jq('#clinicClosedDiv').hide();
    }
  } else {
    jq('#extraInfoDiv').hide();
  }

  if (shouldOutsideDateWindowInformationBeDisplayed) {
    jq('#outsideDateWindowDiv').show();
    jq('#outsideDateWindowInfo').append(emr.message('visits.outsideDateWindowInfoMessage'));
  } else {
    jq('#outsideDateWindowDiv').hide();
  }
}

function getNumberOfDaysBetweenDates(date1, date2) {
  if (date1 && date2) {
    return Math.abs((Date.UTC(date2.getFullYear(), date2.getMonth(), date2.getDate()) -
      Date.UTC(date1.getFullYear(), date1.getMonth(), date1.getDate()))) / (24 * 60 * 60 * 1000);
  }
  return null;
}

function findClosestPreviousVisit(allVisitDates, currentVisitDate) {
  const previousVisitDates = allVisitDates.filter(date => date < currentVisitDate);
  if (!previousVisitDates.length) {
    return null;
  }

  return new Date(Math.max.apply(null, previousVisitDates));
}

function findClosestFutureVisit(allVisitDates, currentVisitDate) {
  const futureVisitDates = allVisitDates.filter(date => date > currentVisitDate);
  if (!futureVisitDates.length) {
    return null;
  }

  return new Date(Math.min.apply(null, futureVisitDates));
}

function onSelectChange(event) {
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

function isEditVisitFormValid() {
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

function enableSaveButton() {
  jq('#save-button').removeClass('disabled-button');
  jq('#save-button').attr('disabled', false);
}
