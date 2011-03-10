/**
 * Validates a date
 */
function checkdate(day,month,year) {

    if (day == "" || month == "" || year == "") {
        return false;
    }
    
    if(!isNumber(day) || !isNumber(month) || !isNumber(year)) {
        return false;
    }

    day=parseInt(day,10);
    month=parseInt(month,10);
    year=parseInt(year,10);
    
    if (day<1 || day>31 || month<1 || month>12)
        return false;

    //advanced error checking months with 30 days
    if (month==4 || month==6 || month==9 || month==11){
        if (day==31)
            return false;
    }
    // february, leap year
    if (month==2){
        if (day>29)
            return false;
        var bis=parseInt(year/4)
        if (isNaN(bis))
            return false;
        if (day==29 && ((year/4)!=parseInt(year/4)))
            return false;
    }

  return true;
}

/**
 * Validates a time
 */
function checkTime(hour, minute) {

    if (hour == "" || minute == "") {
        return false;
    }

    if(!isNumber(hour) || !isNumber(minute)) {
        return false;
    }

    hour   = parseInt(hour,  10);
    minute = parseInt(minute,10);

    if (hour < 0 || hour > 23) {
        return false;
    }
    if (minute < 0 || minute > 59) {
        return false;
    }

  return true;
}

/**
 * Checks if inputNum is a number
 */
function isNumber(inputNum) {
    var decimalpoint=false;
    for (var i=0; i<inputNum.length;i++) {
        var oneChar=inputNum.charAt(i);
        if (i==0 && oneChar=="-"){
            continue;
        }
        if(oneChar =="." && !decimalpoint){
            decimalpoint=true;
            continue;
        }
        if ((oneChar <"0" ||oneChar>"9")){
            return false;
        }
    }
    return true;
}

/**
 * Compares two numbers
 */
function compareNumber(num1, num2) {
    num1 = parseInt(num1, 10);
    num2 = parseInt(num2, 10);

    if (num2 < num1) {
        return 0;
    } else if (num2 == num1){
        return 1;
    }

    return 2;
}

/**
 * Compares the dates with time
 */
function compareDate(yyyys, mms, dds, hhs, mins, yyyye, mme, dde, hhe, mine) {
    if(compareNumber(yyyys, yyyye) == 0) {
        return false;
    } else if(compareNumber(yyyys, yyyye) == 1){
        if(compareNumber(mms, mme) == 0) {
            return false;
        } else if(compareNumber(mms, mme) == 1){
            if(compareNumber(dds, dde) == 0) {
                return false;
            } else if(compareNumber(dds, dde) == 1){
                if(compareNumber(hhs, hhe) == 0) {
                    return false;
                } else if(compareNumber(hhs, hhe) == 1){
                    if(compareNumber(mins, mine) == 0) {
                        return false;
                    }
                }
            }
        }
    }
    return true;
}

/**
 * Compares the dates without time
 */
function compareDateWithoutTime(yyyys, mms, dds, yyyye, mme, dde) {
    if(compareNumber(yyyys, yyyye) == 0) {
        return false;
    } else if(compareNumber(yyyys, yyyye) == 1){
        if(compareNumber(mms, mme) == 0) {
            return false;
        } else if(compareNumber(mms, mme) == 1){
            if(compareNumber(dds, dde) == 0) {
                return false;
            }
        }
    }
    return true;
}

/**
 * Checks if the inputNum is a valid percent value
 */
function checkPercent(inputNum) {
    if (!isNumber(inputNum)) {
        return false;
    }
    if (inputNum<0 || inputNum>100) {
        return false;
    }
    return true;
}

/**
 * Checks if the inputValue is a valid email address value
 */
function checkEmailAdr(inputValue) {

    //
    //
    // The address should start with a sequence of alphanumerical, underscores,
    // dots or dashes.
    // Then comes an @.
    // After that there should be another sequence of alphanumerical characters
    // and dashes, followed by a dot.
    // And then another sequence of alphanumerical characters, but without the
    // dash.
    var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9])+$/;
    if ( !inputValue.match(filter)) {
       return false;
    }

    var atPos = inputValue.indexOf('@',0);
    if (atPos == -1) {
        return false;
    }
    if (atPos == 0) {
        return false;
    }
    if (inputValue.indexOf('@', atPos + 1) > - 1) {
        return false;
    }
    if (inputValue.indexOf('.', atPos) == -1) {
        return false;
    }
    if (inputValue.indexOf('@.',0) != -1) {
        return false;
    }
    if (inputValue.indexOf('.@',0) != -1){
        return false;
    }
    if (inputValue.indexOf('..',0) != -1) {
        return false;
    }
    return true;
}

/**
 * Checks if the year is between 1900 and 2100
 */
function checkYearRange(inputYear) {
    if (inputYear<1900 || inputYear>2100) {
        return false;
    }
    return true;
}