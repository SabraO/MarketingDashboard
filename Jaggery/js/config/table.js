var populateDashboard = function(title,dashboardData,year,quarter,previousYear,firstDateString,lastDateString,
                                                prevWeekFirstDateString,prevWeekLastDateString,previousQuartersData){

    //Set table headers
    var tableContent = "<tr>";
    tableContent += "<th class='table-title'></th>";
    //Previous quarter data headers
    for(var i=0; i < previousQuartersData.length;i++){
        tableContent += "<th class='table-title'>Q" + previousQuartersData[i].previousYear.quarter + " " +
                                                                previousQuartersData[i].previousYear.year + "</th>";
        tableContent += "<th class='table-title'>Q" + previousQuartersData[i].thisYear.quarter + " " + previousQuartersData[i].thisYear.year
                                                                + "</th>";
    }

    //Normal table headers
    tableContent += "<th class='table-title'>Q" + quarter + " " + previousYear + "</th>";
    tableContent += "<th class='table-title'>Cumulative: Q" + quarter + " " + year + " (as of " + getFormattedDateString(lastDateString) + ")</th>";
    tableContent += "<th class='table-title'>This Week (" + getFormattedDateString(firstDateString) + "-" + getFormattedDateString(lastDateString) + ")</th>";
    tableContent += "<th class='table-title'>Last Week (" + getFormattedDateString(prevWeekFirstDateString) + "-" + getFormattedDateString(prevWeekLastDateString) + ")</th>";
    tableContent += "<th class='table-title'>Q" + quarter + " " + year + " forecast</th>";
    tableContent += "<th class='table-title'>WoW Growth %</th>";
    tableContent += "<th class='table-title'>QTD vs forecast %</th>";
    tableContent += "<th class='table-title'>Q" + quarter + " " + year + " YTD vs Q" + quarter + " " + previousYear + "</th>";
    tableContent += "<th class='table-title'>" + year + "(YTD)</th>";
    tableContent += "<th class='table-title'>" + previousYear + "</th>";
    tableContent += "<th class='table-title'>" + year + " YTD vs " + previousYear + "% change</th>";

    tableContent += "</tr>";

    //Set Website Visitor title
    tableContent += "<tr>";
    tableContent += "<td class='table-boader'>" + title + "</td>";

    for(var j=0; j < 11;j++){
        tableContent += "<td class='table-boader'></td>";
    }

    //Set table content

    //Total
    tableContent += "<tr>";
    tableContent += "<td class='table-boader'>Total</td>";
    for(var k=0; k< previousQuartersData.length; k++){
        tableContent += "<td class='table-boader'>" + numberWithCommas(previousQuartersData[k].previousYear.total_users) + "</td>";
        tableContent += "<td class='table-boader'>" + numberWithCommas(previousQuartersData[k].thisYear.total_users) + "</td>";
    }

    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.prevYear_Quarter.total) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.cumulative_Quarter.total) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.thisWeek.total) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.lastWeek.total) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.forecast_Quarter.total) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.WoWGrowth.total) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.qtdVsForecast.total) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.ytdVsQuarter.total) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.thisYear_ytd.total) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.prevYear_total.total) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.thisYearVsPrevYear.total) + "</td>";

    tableContent += "</tr>";

    //NA
    tableContent += "<tr>";
    tableContent += "<td class='table-boader'>US & Canada</td>";
    for(var i=0; i< previousQuartersData.length; i++){
        tableContent += "<td class='table-boader'>" + numberWithCommas(previousQuartersData[i].previousYear.na_users) + "</td>";
        tableContent += "<td class='table-boader'>" + numberWithCommas(previousQuartersData[i].thisYear.na_users) + "</td>";
    }

    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.prevYear_Quarter.na) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.cumulative_Quarter.na) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.thisWeek.na) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.lastWeek.na) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.forecast_Quarter.na) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.WoWGrowth.na) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.qtdVsForecast.na) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.ytdVsQuarter.na) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.thisYear_ytd.na) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.prevYear_total.na) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.thisYearVsPrevYear.na) + "</td>";

    tableContent += "</tr>";

    //NA Per
    tableContent += "<tr>";
    tableContent += "<td class='table-boader'><b>%US & Canada</b></td>";
    for(var l=0; l< previousQuartersData.length; l++){
        tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(previousQuartersData[l].previousYear.naPer) + "</td>";
        tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(previousQuartersData[l].thisYear.naPer) + "</td>";
    }

    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.prevYear_Quarter.naPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.cumulative_Quarter.naPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.thisWeek.naPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.lastWeek.naPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.forecast_Quarter.naPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.WoWGrowth.naPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.qtdVsForecast.naPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.ytdVsQuarter.naPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.thisYear_ytd.naPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.prevYear_total.naPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.thisYearVsPrevYear.naPer) + "</td>";

    tableContent += "</tr>";

    //EU
    tableContent += "<tr>";
    tableContent += "<td class='table-boader'>EU</td>";
    for(var p=0; p< previousQuartersData.length; p++){
        tableContent += "<td class='table-boader'>" + numberWithCommas(previousQuartersData[p].previousYear.eu) + "</td>";
        tableContent += "<td class='table-boader'>" + numberWithCommas(previousQuartersData[p].thisYear.eu) + "</td>";
    }

    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.prevYear_Quarter.eu) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.cumulative_Quarter.eu) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.thisWeek.eu) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.lastWeek.eu) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.forecast_Quarter.eu) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.WoWGrowth.eu) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.qtdVsForecast.eu) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.ytdVsQuarter.eu) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.thisYear_ytd.eu) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.prevYear_total.eu) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.thisYearVsPrevYear.eu) + "</td>";

    tableContent += "</tr>";

    //EU Per
    tableContent += "<tr>";
    tableContent += "<td class='table-boader'><b>%EU</b></td>";
    for(var q=0; q< previousQuartersData.length; q++){
        tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(previousQuartersData[q].previousYear.euPer) + "</td>";
        tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(previousQuartersData[q].thisYear.euPer) + "</td>";
    }

    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.prevYear_Quarter.euPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.cumulative_Quarter.euPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.thisWeek.euPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.lastWeek.euPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.forecast_Quarter.euPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.WoWGrowth.euPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.qtdVsForecast.euPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.ytdVsQuarter.euPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.thisYear_ytd.euPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.prevYear_total.euPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.thisYearVsPrevYear.euPer) + "</td>";

    tableContent += "</tr>";

    //Rest
    tableContent += "<tr>";
    tableContent += "<td class='table-boader'>Rest</td>";
    for(var n=0; n< previousQuartersData.length; n++){
        tableContent += "<td class='table-boader'>" + numberWithCommas(previousQuartersData[n].previousYear.rest) + "</td>";
        tableContent += "<td class='table-boader'>" + numberWithCommas(previousQuartersData[n].thisYear.rest) + "</td>";
    }

    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.prevYear_Quarter.rest) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.cumulative_Quarter.rest) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.thisWeek.rest) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.lastWeek.rest) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.forecast_Quarter.rest) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.WoWGrowth.rest) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.qtdVsForecast.rest) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.ytdVsQuarter.rest) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.thisYear_ytd.rest) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.prevYear_total.rest) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.thisYearVsPrevYear.rest) + "</td>";

    tableContent += "</tr>";

    //Rest Per
    tableContent += "<tr>";
    tableContent += "<td class='table-boader'><b>%Rest</b></td>";
    for(var r=0; r< previousQuartersData.length; r++){
        tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(previousQuartersData[r].previousYear.restPer) + "</td>";
        tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(previousQuartersData[r].thisYear.restPer) + "</td>";
    }

    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.prevYear_Quarter.restPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.cumulative_Quarter.restPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.thisWeek.restPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.lastWeek.restPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.forecast_Quarter.restPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.WoWGrowth.restPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.qtdVsForecast.restPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.ytdVsQuarter.restPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.thisYear_ytd.restPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.prevYear_total.restPer) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.thisYearVsPrevYear.restPer) + "</td>";

    tableContent += "</tr>";

    //Unclassified
    tableContent += "<tr>";
    tableContent += "<td class='table-boader'>Unclassified</td>";
    for(var h=0; h< previousQuartersData.length; h++){
        tableContent += "<td class='table-boader'>" + numberWithCommas(previousQuartersData[h].previousYear.unclassified) + "</td>";
        tableContent += "<td class='table-boader'>" + numberWithCommas(previousQuartersData[h].thisYear.unclassified) + "</td>";
    }

    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.prevYear_Quarter.unclassified) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.cumulative_Quarter.unclassified) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.thisWeek.unclassified) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.lastWeek.unclassified) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.forecast_Quarter.unclassified) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.WoWGrowth.unclassified) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.qtdVsForecast.unclassified) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.ytdVsQuarter.unclassified) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.thisYear_ytd.unclassified) + "</td>";
    tableContent += "<td class='table-boader'>" + numberWithCommas(dashboardData.prevYear_total.unclassified) + "</td>";
    tableContent += "<td class='table-boader'>" + formatToTwoDecimalPlaces(dashboardData.thisYearVsPrevYear.unclassified) + "</td>";

    tableContent += "</tr>";

    return tableContent;

};

var numberWithCommas = function(x) {
    if(x!=null) {
        var parts = x.toString().split(".");
        parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        return parts.join(".");
    }
    else{
        return 0;
    }
};

var formatToTwoDecimalPlaces = function (x) {
    if(x!=0){
        return parseFloat(Math.round(x * 100) / 100).toFixed(2);
    }
    else{
        return 0;
    }

};

var getFormattedDateString = function (dateString) {

    var data = dateString.split("-");

    var month, suffix = "th";

    switch (data[1]) {
        case "01": month = "Jan "; break;
        case "02": month = "Feb "; break;
        case "03": month = "Mar "; break;
        case "04": month = "Apr "; break;
        case "05": month = "May "; break;
        case "06": month = "Jun "; break;
        case "07": month = "Jul "; break;
        case "08": month = "Aug "; break;
        case "09": month = "Sep "; break;
        case "10": month = "Oct "; break;
        case "11": month = "Nov "; break;
        case "12": month = "Dec "; break;
    }

    if(data[2]=="1"||data[2]=="21" ||data[2]=="31") suffix = "st";
    else if(data[2]=="2"||data[2]=="22") suffix = "nd";
    else if(data[2]=="3" || data[2]=="23") suffix = "rd";

    return month + data[2] + suffix;
};