var getColumnData = function(queryResult){

    var columnData = {
        "total" : 0,
        "na" : 0,
        "naPer" : 0,
        "eu" : 0,
        "euPer" : 0,
        "rest" : 0,
        "restPer" : 0,
        "unclassified" : 0
    };

    var total, na, eu, rest, unclassified, naPer, euPer, restPer;

    total = queryResult[0].total_users;
    na = queryResult[0].na_users;
    eu = queryResult[0].eu_users;
    rest = queryResult[0].row_users;
    unclassified = queryResult[0].unclassified_users;

    naPer = (na/total) * 100;
    euPer = (eu/total) * 100;
    restPer = (rest/total) * 100;

    columnData.total = total;
    columnData.na = na;
    columnData.naPer = naPer;
    columnData.eu = eu;
    columnData.euPer = euPer;
    columnData.rest = rest;
    columnData.restPer = restPer;
    columnData.unclassified = unclassified;

    return columnData;
};

var getCalculatedData = function(queryResult1,queryResult2){

    var columnData = {
        "total" : 0,
        "na" : 0,
        "naPer" : 0,
        "eu" : 0,
        "euPer" : 0,
        "rest" : 0,
        "restPer" : 0,
        "unclassified" : 0
    };

    var total, na, eu, rest;

    total = ( (queryResult1[0].total_users - queryResult2[0].total_users)/ queryResult2[0].total_users ) * 100;
    na = ( (queryResult1[0].na_users - queryResult2[0].na_users)/ queryResult2[0].na_users ) * 100;
    eu = ( (queryResult1[0].eu_users - queryResult2[0].eu_users)/ queryResult2[0].eu_users ) * 100;
    rest = ( (queryResult1[0].row_users - queryResult2[0].row_users)/ queryResult2[0].row_users ) * 100;

    columnData.total = total;
    columnData.na = na;
    columnData.eu = eu;
    columnData.rest = rest;

    return columnData;
};

var getDashboardData = function(databaseType,leadType,year,previousYear,quarter,firstDateString,lastDateString,
                                                            previousWeekFirstDateString, previousWeekLastDateString,
                                                                            quarterlyForecast){

    var dashboardData = {
        "prevYear_Quarter" : {},
        "cumulative_Quarter" : {},
        "thisWeek" : {},
        "lastWeek" : {},
        "forecast_Quarter" : {},
        "WoWGrowth" : {},
        "qtdVsForecast" : {},
        "ytdVsQuarter" : {},
        "thisYear_ytd" : {},
        "prevYear_total" : {},
        "thisYearVsPrevYear" : {}
    };
    var total_users, eu_users, na_users, row_users, unclassified_users;

    //Get values from database

    //1 - Quarter
    var queryResult1 = db.query("SELECT * FROM `" + databaseType + "_Quarterly` WHERE `year` = " + previousYear
                                                                                        +" AND `quarter`= " + quarter);
    dashboardData.prevYear_Quarter = getColumnData(queryResult1);

    //3 - This Week
    var queryResult2 = db.query("SELECT * FROM `" + databaseType + "_Weekly` WHERE `start_date` = \"" + firstDateString
                                                                    + "\" AND `end_date` = \"" + lastDateString + "\"");
    dashboardData.thisWeek = getColumnData(queryResult2);

    //2 - Cumulative
    var queryResult3 = db.query("SELECT * FROM `" + databaseType + "_CumulativeQuarterly` WHERE `end_date` = \"" +
                                previousWeekLastDateString + "\" AND `year` = " + year + " AND `quarter` = " + quarter);

    if(queryResult3.length!=0) {
        total_users = queryResult2[0].total_users + queryResult3[0].total_users;
        eu_users = queryResult2[0].eu_users + queryResult3[0].eu_users;
        na_users = queryResult2[0].na_users + queryResult3[0].na_users;
        row_users = queryResult2[0].row_users + queryResult3[0].row_users;
        unclassified_users = queryResult2[0].unclassified_users + queryResult3[0].unclassified_users;

        //update cumulative quarterly table
        db.query("UPDATE `" + databaseType + "_CumulativeQuarterly` SET `end_date`= \"" + lastDateString
        + "\",`total_users`=" + total_users + ",`eu_users`=" + eu_users + ",`na_users`=" + na_users + ",`row_users`="
        + row_users + ",`unclassified_users`=" + unclassified_users + " WHERE `year` = " + year + " AND `quarter` = "
        + quarter);
    }

    var queryResult4 = db.query("SELECT * FROM `" + databaseType + "_CumulativeQuarterly` WHERE `end_date` = \""
                                                                                            + lastDateString + "\"");
    dashboardData.cumulative_Quarter = getColumnData(queryResult4);

    //4 - Last Week
    var queryResult5 = db.query("SELECT * FROM `" + databaseType + "_Weekly` WHERE `start_date` = \"" +
                            previousWeekFirstDateString + "\" AND `end_date` = \"" + previousWeekLastDateString + "\"");
    dashboardData.lastWeek = getColumnData(queryResult5);

    //9 - YTD
    var queryResult6 = db.query("SELECT * FROM `" + databaseType + "_CumulativeYearly` WHERE `year` = " + year
                                                        +" AND `end_date` = \"" + previousWeekLastDateString + "\"" );

    if(queryResult6.length!=0){
        total_users = queryResult2[0].total_users + queryResult6[0].total_users;
        eu_users = queryResult2[0].eu_users + queryResult6[0].eu_users;
        na_users = queryResult2[0].na_users + queryResult6[0].na_users;
        row_users = queryResult2[0].row_users + queryResult6[0].row_users;
        unclassified_users = queryResult2[0].unclassified_users + queryResult6[0].unclassified_users;

        //update cumulative yearly table
        db.query("UPDATE `" + databaseType + "_CumulativeYearly` SET `end_date`= \"" + lastDateString +
        "\",`total_users`=" + total_users + ",`eu_users`=" + eu_users + ",`na_users`=" + na_users + ",`row_users`="
                            + row_users + ",`unclassified_users`=" + unclassified_users + " WHERE `year` = " + year);
    }

    var queryResult7 = db.query("SELECT * FROM `" + databaseType + "_CumulativeYearly` WHERE `year` = " + year +
                                                                    " AND `end_date` = \"" + lastDateString + "\"" );
    dashboardData.thisYear_ytd = getColumnData(queryResult7);

    //10 - 2014
    var queryResult8 = db.query("SELECT * FROM `" + databaseType + "_Yearly` WHERE `year` = " + previousYear);
    dashboardData.prevYear_total = getColumnData(queryResult8);

    //5 - Forecast
    var forecast = quarterlyForecast;
    var forecastnaPer = (forecast.na/forecast.total) * 100;
    var forecasteuPer = (forecast.eu/forecast.total) * 100;
    var forecastrestPer = (forecast.rest/forecast.total) * 100;

    dashboardData.forecast_Quarter = {
        "total" : forecast.total,
        "na" : forecast.na,
        "naPer" : forecastnaPer,
        "eu" : forecast.eu,
        "euPer" : forecasteuPer,
        "rest" : forecast.rest,
        "restPer" : forecastrestPer,
        "unclassified" : forecast.unclassified
    };

    //Calculate from values obtained from database and conf file

    //6 - WoW Growth
    dashboardData.WoWGrowth = getCalculatedData(queryResult2,queryResult5);

    //7 - QTD vs Forecast
    var total, na, eu, rest;

    total = (queryResult4[0].total_users/forecast.total) * 100;
    na = (queryResult4[0].na_users/forecast.na) * 100;
    eu = (queryResult4[0].eu_users/forecast.eu) * 100;
    rest = (queryResult4[0].row_users/forecast.rest) * 100;

    dashboardData.qtdVsForecast = {
        "total" : total,
        "na" : na,
        "naPer" : 0,
        "eu" : eu,
        "euPer" : 0,
        "rest" : rest,
        "restPer" : 0,
        "unclassified" : 0
    };

    //8 - Q1 2015 YTD vs Q1 2014
    dashboardData.ytdVsQuarter = getCalculatedData(queryResult4,queryResult1);

    //11 - 2015 YTD vs 2014 per
    dashboardData.thisYearVsPrevYear = getCalculatedData(queryResult7,queryResult8);

    return dashboardData;
};

var setQuarterlyData = function(databaseType,quarter,previousQuarter,insertYear,updateYear,lastDateString){

    var total_users,eu_users,na_users,row_users,unclassified_users;

    var queryResult = db.query("SELECT * FROM `" + databaseType + "_CumulativeQuarterly` WHERE `year` = " + insertYear
                                    + " AND `quarter` = " + quarter + " AND `end_date` = \"" + lastDateString + "\"");

    if(queryResult.length==0) {
        //Insert record for new quarter in cumulative quarterly
        var queryResult1 = db.query("SELECT * FROM `" + databaseType +"_Quarterly` WHERE `quarter` = " + quarter +
                                                                                        " AND `year` = " + insertYear);

        total_users = queryResult1[0].total_users;
        eu_users = queryResult1[0].eu_users;
        na_users = queryResult1[0].na_users;
        row_users = queryResult1[0].row_users;
        unclassified_users = queryResult1[0].unclassified_users;

        db.query("INSERT INTO `" + databaseType + "_CumulativeQuarterly`(`year`, `quarter`, `end_date`, `total_users`,"+
        " `eu_users`, `na_users`, `row_users`, `unclassified_users`) VALUES (" + insertYear + "," + quarter + ",\"" +
                        lastDateString + "\"," + total_users + "," + eu_users + "," + na_users + "," + row_users + "," +
                                                                                            unclassified_users + ")");
    }

    queryResult = db.query("SELECT * FROM `" + databaseType +"_Quarterly` WHERE `year` = " + updateYear +
                                                                                " AND `quarter` = " + previousQuarter );

    if(queryResult.length==0) {
        //Update quarterly
        var queryResult2 = db.query("SELECT * FROM `" + databaseType + "_Quarterly` WHERE `quarter` = " +
                                                                    previousQuarter + " AND `year` = " + updateYear);
        var queryResult3 = db.query("SELECT * FROM `" + databaseType + "_CumulativeQuarterly` WHERE `quarter` = "
                                                                    + previousQuarter + " AND `year` = " + updateYear);

        total_users = queryResult2[0].total_users + queryResult3[0].total_users;
        eu_users = queryResult2[0].eu_users + queryResult3[0].eu_users;
        na_users = queryResult2[0].na_users + queryResult3[0].na_users;
        row_users = queryResult2[0].row_users + queryResult3[0].row_users;
        unclassified_users = queryResult2[0].unclassified_users + queryResult3[0].unclassified_users;

        db.query("UPDATE `" + databaseType +"_Quarterly` SET `total_users`=" + total_users + ",`eu_users`=" + eu_users +
        ",`na_users`=" + na_users + ",`row_users`=" + row_users + ",`unclassified_users`=" + unclassified_users +
        " WHERE `year` = " + updateYear + " AND `quarter` = " + previousQuarter);
    }
};

var setYearlyData = function(databaseType,year,previousYear,prevQuarter,lastDateString){

    var total_users,eu_users,na_users,row_users,unclassified_users;

    var queryResult = db.query("SELECT * FROM `" + databaseType +"_CumulativeYearly` WHERE `year` = " + year +
                                                                    " AND `end_date` = \"" + lastDateString + "\"" );

    if(queryResult.length==0) {

        //Insert record for new year in cumulative yearly
        var queryResult1 = db.query("SELECT * FROM `" + databaseType + "_Yearly` WHERE `year` = " + year);

        total_users = queryResult1[0].total_users;
        eu_users = queryResult1[0].eu_users;
        na_users = queryResult1[0].na_users;
        row_users = queryResult1[0].row_users;
        unclassified_users = queryResult1[0].unclassified_users;

        db.query("INSERT INTO `" + databaseType +"_CumulativeYearly`(`year`, `end_date`, `total_users`, `eu_users`, " +
        "`na_users`, `row_users`, `unclassified_users`) VALUES (" + year + ",\"" + lastDateString + "\"," + total_users
        + "," + eu_users + "," + na_users + "," + row_users + "," + unclassified_users + ")");
    }

    queryResult = db.query("SELECT * FROM `" + databaseType + "_Yearly` WHERE `year` = " + previousYear);

    if(queryResult.length==0) {
        //Insert new record into yearly for previous year
        var queryResult2 = db.query("SELECT * FROM `" + databaseType + "_Quarterly` WHERE `quarter` = " + prevQuarter
                                                                                    + " AND `year` = " + previousYear);
        var queryResult3 = db.query("SELECT * FROM `" + databaseType +"_CumulativeYearly` WHERE `year` = "
                                                                                                        + previousYear);

        total_users = queryResult2[0].total_users + queryResult3[0].total_users;
        eu_users = queryResult2[0].eu_users + queryResult3[0].eu_users;
        na_users = queryResult2[0].na_users + queryResult3[0].na_users;
        row_users = queryResult2[0].row_users + queryResult3[0].row_users;
        unclassified_users = queryResult2[0].unclassified_users + queryResult3[0].unclassified_users;

        db.query("INSERT INTO `" + databaseType + "_Yearly`(`year`, `total_users`, `eu_users`, `na_users`, `row_users`," +
        " `unclassified_users`) VALUES (" + previousYear + "," + total_users + "," + eu_users + "," + na_users + "," +
        row_users + "," + unclassified_users + ")");
    }
};

var getPrevQuarterData = function(databaseType,quarter){
    var previousQuartersData = [];

    if(quarter!=1){
        var queryResult;
        var previousQuarterData = {
            "previousYear" : {
                "year" : "",
                "quarter" : 0,
                "columnData" : {}
            },
            "thisYear" : {
                "year" : "",
                "quarter" : 0,
                "columnData" : {}
            }
        };
        for(var i = 1; i < quarter;i++){
            queryResult = db.query("SELECT * FROM `" + databaseType + "_Quarterly` WHERE `year` = " + previousYear +
                                                                                            " AND `quarter` = " + i);

            previousQuarterData.previousYear.year = previousYear;
            previousQuarterData.previousYear.quarter = i;
            previousQuarterData.previousYear.columnData = getColumnData(queryResult[0]);

            queryResult = db.query("SELECT * FROM `" + databaseType + "_Quarterly` WHERE `year` = " + year +
                                                                                            " AND `quarter` = " + i);
            previousQuarterData.thisYear.year = year;
            previousQuarterData.thisYear.quarter = i;
            previousQuarterData.thisYear.columnData = getColumnData(queryResult[0]);

            previousQuartersData.push(previousQuarterData);
        }
    }
    return previousQuartersData;
};

var getWeeklyData = function(databaseType){
    var weeklyData = [];

    for(var i =0 ; i< dateRanges.length;i++){

        var queryResult = db.query("SELECT * FROM `" + databaseType + "_Weekly` WHERE `start_date` = \"" +
                                dateRanges[i].firstDate + "\" AND `end_date` = \"" + dateRanges[i].lastDate + "\"");
        weeklyData.push(queryResult[0]);
    }

    return weeklyData;
};

var getYearlyData = function(databaseType){

    var yearlyData = {
        "prevYear" : {},
        "thisYear" : {}
    };

    var queryResultY1 = db.query("SELECT * FROM `" + databaseType + "_Yearly` WHERE `year` = " + previousYear);
    yearlyData.prevYear = queryResultY1;

    var queryResultY2 = db.query("SELECT * FROM `" + databaseType + "_Yearly` WHERE `year` = " + year);
    yearlyData.thisYear = queryResultY2;

    return yearlyData;
};

var getQuarterlyData_ThisYear = function(prevQData,dashboardData){
    var quarterlyData_ThisYear = [];

    for(var i =0 ; i < prevQData.length;i++){
        quarterlyData_ThisYear.push(prevQData[i]);
    }

    quarterlyData_ThisYear.push(dashboardData.cumulative_Quarter);

    return quarterlyData_ThisYear;
};

var getQuarterlyData_PrevYear = function(databaseType){
    var quarterlyData_PreviousYear = [];

    var queryResult;
    for(var i = 0; i < quarter;i++){
        queryResult = db.query("SELECT * FROM `" + databaseType +"_Quarterly` WHERE `year` = " + previousYear +
                                                                                        " AND `quarter` = " + (i+1) );
        quarterlyData_PreviousYear.push(dataManipulation.getColumnData(queryResult));
    }

    return quarterlyData_PreviousYear;

};

