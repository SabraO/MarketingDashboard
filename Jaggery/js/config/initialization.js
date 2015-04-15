//Initialize the database
var db = new Database("jdbc:mysql://localhost:3306/WSO2MarketingDashboardData", "root", "root");

var getDatabase = function () {
    return db;
};

var database = function(databaseType, leadType){

    //1.1 - quarterly data
    var quarterly = leadType.quarterly;
    var result = db.query("SELECT * FROM `" + databaseType + "_Quarterly` WHERE `year` = " + quarterly.year +
    " AND `quarter` = "+ quarterly.quarter);
    //initialize quarterly
    if(result.length==0){
        db.query("INSERT INTO `"+databaseType+"_Quarterly`(`year`, `quarter`, `total_users`, `eu_users`, `na_users`," +
        " `row_users`, `unclassified_users`) VALUES (" + quarterly.year + "," + quarterly.quarter + "," +
        quarterly.total + "," + quarterly.eu + "," + quarterly.na + "," + quarterly.rest + "," +
        quarterly.unclassified + ")");
    }

    //1.2 - cumulative quarter data
    var cumulative = leadType.cumulative;
    result =  db.query("SELECT * FROM `" + databaseType + "_CumulativeQuarterly` WHERE `year` = " + cumulative.year +
    " AND `quarter` = " + cumulative.quarter);

    //initialize cumulative quarterly
    if(result.length==0){
        db.query("INSERT INTO `" + databaseType + "_CumulativeQuarterly`(`year`,`quarter`,`end_date`,`total_users`, " +
        "`eu_users`, `na_users`, `row_users`, `unclassified_users`) VALUES (" + cumulative.year + "," +
        cumulative.quarter + ",\"" + cumulative.end_date + "\"," + cumulative.total + "," + cumulative.eu + "," +
        cumulative.na + "," + cumulative.rest + "," + cumulative.unclassified + ")");
    }

    //1.3 This week data
    var thisWeek = leadType.thisWeek;
    result = db.query("SELECT * FROM `" + databaseType + "_Weekly` WHERE `start_date` = \"" + thisWeek.start_date +
    "\" AND `end_date` = \"" + thisWeek.end_date + "\"");

    //initialize weekly
    if(result.length==0){
        db.query("INSERT INTO `" + databaseType + "_Weekly`(`start_date`, `end_date`, `total_users`, `eu_users`, " +
        "`na_users`, `row_users`, `unclassified_users`) VALUES (\"" + thisWeek.start_date + "\",\"" + thisWeek.end_date
        + "\"," + thisWeek.total + "," + thisWeek.eu + "," + thisWeek.na + "," + thisWeek.rest + "," +
        thisWeek.unclassified + ")");
    }

    //1.4 Last week data
    var lastWeek = leadType.lastWeek;
    result = db.query("SELECT * FROM `" + databaseType + "_Weekly` WHERE `start_date` = \"" + lastWeek.start_date +
    "\" AND `end_date` = \"" + lastWeek.end_date + "\"");

    //initialize weekly
    if(result.length==0){
        db.query("INSERT INTO `" + databaseType + "_Weekly`(`start_date`, `end_date`, `total_users`, `eu_users`, " +
        "`na_users`, `row_users`, `unclassified_users`) VALUES (\"" + lastWeek.start_date + "\",\"" + lastWeek.end_date
        + "\"," + lastWeek.total + "," + lastWeek.eu + "," + lastWeek.na + "," + lastWeek.rest + "," +
        lastWeek.unclassified + ")");
    }

    //1.5 Cumulative yearly data 2015
    var ytd = leadType.ytd;
    result = db.query("SELECT * FROM `" + databaseType + "_CumulativeYearly` WHERE `year` = " + ytd.year);

    //initialize cumulative yearly
    if(result.length==0){
        db.query("INSERT INTO `" + databaseType +"_CumulativeYearly`(`year`, `end_date` , `total_users`, `eu_users`, " +
        "`na_users`, `row_users`, `unclassified_users`) VALUES (" + ytd.year + ",\"" + ytd.end_date + "\"," + ytd.total
        + "," + ytd.eu + "," + ytd.na + "," + ytd.rest + "," + ytd.unclassified + ")");
    }

    //1.6 Yearly data 2014
    var yearly =  leadType.yearly;
    result = db.query("SELECT * FROM `" + databaseType + "_Yearly` WHERE `year` = " + yearly.year);

    if(result.length==0){
        db.query("INSERT INTO `" +databaseType+ "_Yearly`(`year`, `total_users`, `eu_users`, `na_users`, `row_users`," +
        " `unclassified_users`) VALUES (" + yearly.year + "," + yearly.total + "," + yearly.eu + "," + yearly.na
        + "," + yearly.rest + "," + yearly.unclassified + ")");
    }

};

var dashboardQueryParameters = function(year,lastDate,lastDateString) {

    //Set dashboard query parameters
    var dashboardQueryResult = db.query("SELECT * FROM `DashboardQueryParameters` WHERE `year` = " + year +
                                                                    " AND `lastDate` = \"" + lastDateString + "\"");

    if(dashboardQueryResult.length==0){
        //Get first date of this week
        var firstDate = new Date();
        firstDate.setDate(lastDate.getDate() - 6);

        //Date range for last week
        var previousWeekFirstDate = new Date();
        previousWeekFirstDate.setDate(firstDate.getDate()-7);

        var previousWeekLastDate = new Date();
        previousWeekLastDate.setDate(lastDate.getDate()-7);

        //Get current and previous year
        var date = new Date();
        year = "" + date.getFullYear();
        date.setFullYear(date.getFullYear()-1);

        var previousYear = date.getFullYear();

        var quarter = getQNumforDate(lastDate);

        var firstDateString = yyyymmdd(firstDate);
        var previousWeekFirstDateString = yyyymmdd(previousWeekFirstDate);
        var previousWeekLastDateString = yyyymmdd(previousWeekLastDate);

        //Insert into dashboard query parameters table
        db.query("INSERT INTO `DashboardQueryParameters`(`year`, `previousYear`, `quarter`, `firstDate`, `lastDate`, " +
                "`prevWeekFirstDate`, `prevWeekLastDate`) VALUES (" + year + "," +previousYear + "," + quarter + ",\"" +
                        firstDateString + "\",\"" + lastDateString + "\",\"" + previousWeekFirstDateString + "\",\"" +
                                                                                previousWeekLastDateString + "\")");
    }
};

var yyyymmdd = function(date) {

    var yyyy = date.getFullYear().toString();
    var mm = (date.getMonth()+1).toString(); // getMonth() is zero-based
    var dd  = date.getDate().toString();

    return yyyy + '-' + (mm[1]?mm:"0"+mm[0]) + '-' + (dd[1]?dd:"0"+dd[0]);
};

var getQNumforDate = function(date){
    return Math.floor((date.getMonth() + 3) / 3);
};

