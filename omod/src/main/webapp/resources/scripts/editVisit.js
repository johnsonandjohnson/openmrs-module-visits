var userLocale = null;

const DISPLAY_FORMAT_DATE = 'DD MMMM YYYY';
const SERVER_FORMAT_DATE = 'YYYY-MM-DD';

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
    "cfl.weekDay.Saturday.fullName"
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

editVisit.showEditVisitDialog = function(isExtraInfoDialogEnabled, holidayWeekdays, allVisitDates, visitUuid,
                                  patientUuid, visitDate, visitTime, visitLocation, visitType, visitStatus) {
  editVisit.createEditVisitDialog(isExtraInfoDialogEnabled, holidayWeekdays, allVisitDates, visitUuid,
                                  patientUuid, visitDate);

  editVisit.editVisitDialog.show();

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

  jq('.error-label').hide();
  enableSaveButton();
}

editVisit.createEditVisitDialog = function(isExtraInfoDialogEnabled, holidayWeekdays, allVisitDates, visitUuid, patientUuid, visitDate) {
  editVisit.editVisitDialog = emr.setupConfirmationDialog({
    selector: '#edit-visit-dialog',
    actions: {
      confirm: function() {
      if (isExtraInfoDialogEnabled) {
        editVisit.showExtraInfoDialog(allVisitDates, holidayWeekdays, visitUuid, patientUuid, visitDate);
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
            success: function() {
              emr.successMessage("visits.genericSuccess");
              editVisit.editVisitDialog.close();
              location.reload();
            },
            error: function() {
              emr.errorMessage("visits.genericFailure");
              editVisit.editVisitDialog.close();
            }
          });
        }
      },
      cancel: function() {
        editVisit.editVisitDialog.close();
      }
    }
  })
};

editVisit.showExtraInfoDialog = function(allVisitDates, holidayWeekdays, visitUuid, patientUuid, visitDate) {
  editVisit.createExtraInfoDialog(visitUuid, patientUuid);

  jq('#extra-info-dialog').show();
  jq('#infoMessagePart1').text('');
  jq('#infoMessagePart2').text('');
  jq('#infoMessagePart1').css('color', '#333333');
  setExtraInfoDialogContent(allVisitDates, holidayWeekdays, visitDate);
}

editVisit.createExtraInfoDialog = function(visitUuid, patientUuid) {
  editVisit.extraInfoDialog = emr.setupConfirmationDialog({
    selector: '#extra-info-dialog',
    actions: {
      confirm: function() {
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
      cancel: function() {
        jq('#extra-info-dialog').hide();
      }
    }
  })
};

editVisit.handleDateInputOnClick = function() {
  jq('.datepicker').show();
}

function getCurrentDateInServerFormat() {
  moment.locale(userLocale);
  const currentDateInputValue = moment(jq('#visit-date-select').val(), DISPLAY_FORMAT_DATE);

  return moment(currentDateInputValue).format(SERVER_FORMAT_DATE);
}

function setExtraInfoDialogContent(allVisitDates, holidayWeekdays, visitDate) {
  const allVisitDatesArray = allVisitDates.split(',');
  allVisitDatesArray.splice(allVisitDatesArray.indexOf(visitDate), 1);

  const newVisitDate = moment(jq('#visit-date-select').datepicker('getDate')).format(SERVER_FORMAT_DATE);
  const isVisitDateDuplicated = allVisitDatesArray.includes(newVisitDate);
  if (isVisitDateDuplicated) {
    allVisitDatesArray.splice(allVisitDatesArray.indexOf(newVisitDate), 1);
  }

  moment.locale(userLocale);
  const currentVisitDate = moment(jq('#visit-date-select').val(), DISPLAY_FORMAT_DATE);
  const dateInEnglish = currentVisitDate.locale('en').format(DISPLAY_FORMAT_DATE);
  const currentVisitWeekDayName = moment(dateInEnglish).locale('en').format('dddd');
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