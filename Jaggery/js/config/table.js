var populateDashboard = function(title,dashboardData,year,quarter,previousYear,firstDateString,lastDateString,
                                                prevWeekFirstDateString,prevWeekLastDateString,previousQuartersData){

    //Set table headers
    var tableContent = "<tr>";
    tableContent += "<th></th>";
    //Previous quarter data headers
    for(var i=0; i < previousQuartersData.length;i++){
        tableContent += "<th>Q" + previousQuartersData[i].previousYear.quarter + " " +
                                                                previousQuartersData[i].previousYear.year + "</th>";
        tableContent += "<th>Q" + previousQuartersData[i].thisYear.quarter + " " + previousQuartersData[i].thisYear.year
                                                                + "</th>";
    }

    //Normal table headers
    tableContent += "<th>Q" + quarter + " " + previousYear + "</th>";
    tableContent += "<th>Cumulative: Q" + quarter + " " + year + " (as of " + lastDateString + ")</th>";
    tableContent += "<th>This Week (" + firstDateString + " " + lastDateString + ")</th>";
    tableContent += "<th>Last Week (" + prevWeekFirstDateString + " " + prevWeekLastDateString + ")</th>";
    tableContent += "<th>Q" + quarter + " " + year + " forecast</th>";
    tableContent += "<th>WoW Growth %</th>";
    tableContent += "<th>QTD vs forecast %</th>";
    tableContent += "<th>Q" + quarter + " " + year + " YTD vs Q" + quarter + " " + previousYear + "</th>";
    tableContent += "<th>" + year + "(YTD)</th>";
    tableContent += "<th>" + previousYear + "</th>";
    tableContent += "<th>" + year + " YTD vs " + previousYear + "% change</th>";

    tableContent += "</tr>";

    //Set Website Visitor title
    tableContent += "<tr>";
    tableContent += "<td>" + title + "</td>";

    for(var j=0; j < 11;j++){
        tableContent += "<td></td>";
    }

    //Set table content

    //Total
    tableContent += "<tr>";
    tableContent += "<td>Total</td>";
    for(var k=0; k< previousQuartersData.length; k++){
        tableContent += "<td>" + numberWithCommas(previousQuartersData[k].previousYear.total_users) + "</td>";
        tableContent += "<td>" + numberWithCommas(previousQuartersData[k].thisYear.total_users) + "</td>";
    }

    tableContent += "<td>" + numberWithCommas(dashboardData.prevYear_Quarter.total) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.cumulative_Quarter.total) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.thisWeek.total) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.lastWeek.total) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.forecast_Quarter.total) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.WoWGrowth.total) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.qtdVsForecast.total) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.ytdVsQuarter.total) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.thisYear_ytd.total) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.prevYear_total.total) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.thisYearVsPrevYear.total) + "</td>";

    tableContent += "</tr>";

    //NA
    tableContent += "<tr>";
    tableContent += "<td>US & Canada</td>";
    for(var i=0; i< previousQuartersData.length; i++){
        tableContent += "<td>" + numberWithCommas(previousQuartersData[i].previousYear.na_users) + "</td>";
        tableContent += "<td>" + numberWithCommas(previousQuartersData[i].thisYear.na_users) + "</td>";
    }

    tableContent += "<td>" + numberWithCommas(dashboardData.prevYear_Quarter.na) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.cumulative_Quarter.na) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.thisWeek.na) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.lastWeek.na) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.forecast_Quarter.na) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.WoWGrowth.na) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.qtdVsForecast.na) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.ytdVsQuarter.na) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.thisYear_ytd.na) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.prevYear_total.na) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.thisYearVsPrevYear.na) + "</td>";

    tableContent += "</tr>";

    //NA Per
    tableContent += "<tr>";
    tableContent += "<td><b>%US & Canada</b></td>";
    for(var l=0; l< previousQuartersData.length; l++){
        tableContent += "<td>" + formatToTwoDecimalPlaces(previousQuartersData[l].previousYear.naPer) + "</td>";
        tableContent += "<td>" + formatToTwoDecimalPlaces(previousQuartersData[l].thisYear.naPer) + "</td>";
    }

    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.prevYear_Quarter.naPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.cumulative_Quarter.naPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.thisWeek.naPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.lastWeek.naPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.forecast_Quarter.naPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.WoWGrowth.naPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.qtdVsForecast.naPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.ytdVsQuarter.naPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.thisYear_ytd.naPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.prevYear_total.naPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.thisYearVsPrevYear.naPer) + "</td>";

    tableContent += "</tr>";

    //EU
    tableContent += "<tr>";
    tableContent += "<td>EU</td>";
    for(var p=0; p< previousQuartersData.length; p++){
        tableContent += "<td>" + numberWithCommas(previousQuartersData[p].previousYear.eu) + "</td>";
        tableContent += "<td>" + numberWithCommas(previousQuartersData[p].thisYear.eu) + "</td>";
    }

    tableContent += "<td>" + numberWithCommas(dashboardData.prevYear_Quarter.eu) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.cumulative_Quarter.eu) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.thisWeek.eu) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.lastWeek.eu) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.forecast_Quarter.eu) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.WoWGrowth.eu) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.qtdVsForecast.eu) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.ytdVsQuarter.eu) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.thisYear_ytd.eu) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.prevYear_total.eu) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.thisYearVsPrevYear.eu) + "</td>";

    tableContent += "</tr>";

    //EU Per
    tableContent += "<tr>";
    tableContent += "<td><b>%EU</b></td>";
    for(var q=0; q< previousQuartersData.length; q++){
        tableContent += "<td>" + formatToTwoDecimalPlaces(previousQuartersData[q].previousYear.euPer) + "</td>";
        tableContent += "<td>" + formatToTwoDecimalPlaces(previousQuartersData[q].thisYear.euPer) + "</td>";
    }

    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.prevYear_Quarter.euPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.cumulative_Quarter.euPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.thisWeek.euPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.lastWeek.euPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.forecast_Quarter.euPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.WoWGrowth.euPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.qtdVsForecast.euPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.ytdVsQuarter.euPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.thisYear_ytd.euPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.prevYear_total.euPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.thisYearVsPrevYear.euPer) + "</td>";

    tableContent += "</tr>";

    //Rest
    tableContent += "<tr>";
    tableContent += "<td>Rest</td>";
    for(var n=0; n< previousQuartersData.length; n++){
        tableContent += "<td>" + numberWithCommas(previousQuartersData[n].previousYear.rest) + "</td>";
        tableContent += "<td>" + numberWithCommas(previousQuartersData[n].thisYear.rest) + "</td>";
    }

    tableContent += "<td>" + numberWithCommas(dashboardData.prevYear_Quarter.rest) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.cumulative_Quarter.rest) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.thisWeek.rest) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.lastWeek.rest) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.forecast_Quarter.rest) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.WoWGrowth.rest) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.qtdVsForecast.rest) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.ytdVsQuarter.rest) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.thisYear_ytd.rest) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.prevYear_total.rest) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.thisYearVsPrevYear.rest) + "</td>";

    tableContent += "</tr>";

    //Rest Per
    tableContent += "<tr>";
    tableContent += "<td><b>%Rest</b></td>";
    for(var r=0; r< previousQuartersData.length; r++){
        tableContent += "<td>" + formatToTwoDecimalPlaces(previousQuartersData[r].previousYear.restPer) + "</td>";
        tableContent += "<td>" + formatToTwoDecimalPlaces(previousQuartersData[r].thisYear.restPer) + "</td>";
    }

    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.prevYear_Quarter.restPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.cumulative_Quarter.restPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.thisWeek.restPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.lastWeek.restPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.forecast_Quarter.restPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.WoWGrowth.restPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.qtdVsForecast.restPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.ytdVsQuarter.restPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.thisYear_ytd.restPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.prevYear_total.restPer) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.thisYearVsPrevYear.restPer) + "</td>";

    tableContent += "</tr>";

    //Unclassified
    tableContent += "<tr>";
    tableContent += "<td>Unclassified</td>";
    for(var h=0; h< previousQuartersData.length; h++){
        tableContent += "<td>" + numberWithCommas(previousQuartersData[h].previousYear.unclassified) + "</td>";
        tableContent += "<td>" + numberWithCommas(previousQuartersData[h].thisYear.unclassified) + "</td>";
    }

    tableContent += "<td>" + numberWithCommas(dashboardData.prevYear_Quarter.unclassified) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.cumulative_Quarter.unclassified) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.thisWeek.unclassified) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.lastWeek.unclassified) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.forecast_Quarter.unclassified) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.WoWGrowth.unclassified) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.qtdVsForecast.unclassified) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.ytdVsQuarter.unclassified) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.thisYear_ytd.unclassified) + "</td>";
    tableContent += "<td>" + numberWithCommas(dashboardData.prevYear_total.unclassified) + "</td>";
    tableContent += "<td>" + formatToTwoDecimalPlaces(dashboardData.thisYearVsPrevYear.unclassified) + "</td>";

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