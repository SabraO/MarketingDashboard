var drawGraphs = function(quarter,leadType,year,previousYear,weeklyData,dateRanges,forecast_Quarter,qDataThisYear,qDataPrevYear,yearlyData,forecastQ,cumulativeQ){

    //WoW Performance
    var endingDates = [], data_total = [], data_na = [], data_eu = [], data_rest = [], forecast_total = [],
        forecast_na = [], forecast_eu = [], forecast_rest = [];

    var noOfWeeksinQ = 12;

    for(var i=0; i<dateRanges.length;i++){
        endingDates.push(dateRanges[i].lastDate);
    }

    for(var i=0; i< weeklyData.length;i++){
        data_total.push(weeklyData[i].total_users);
        data_na.push(weeklyData[i].na_users);
        data_eu.push(weeklyData[i].eu_users);
        data_rest.push(weeklyData[i].row_users);

        forecast_total.push(forecast_Quarter.total/noOfWeeksinQ);
        forecast_na.push(forecast_Quarter.na/noOfWeeksinQ);
        forecast_eu.push(forecast_Quarter.eu/noOfWeeksinQ);
        forecast_rest.push(forecast_Quarter.rest/noOfWeeksinQ);
    }

    endingDates.reverse();

    var WoWSeries_Comparison = [{ name: 'Actual Total', data: data_total.reverse()},
                                { name: 'Actual EU', data: data_eu.reverse()},
                                { name: 'Actual NA', data: data_na.reverse()},
                                { name: 'Actual REST', data: data_rest.reverse()},
                                { name: 'Forecast Total', data: forecast_total},
                                { name: 'Forecast NA', data: forecast_na},
                                { name: 'Forecast EU', data: forecast_eu},
                                { name: 'Forecast REST', data: forecast_rest}
                                ];

    $(function () {
        $('#WoWPerformanceComparison').highcharts(getLineChartOptions('WoW Performance','Forecast vs Cumulative ' +
                                                            'Comparison',leadType,endingDates,WoWSeries_Comparison));
    });

    //Quarterly Comparison
    var labels = [], prevYear_na = [], prevYear_eu = [], prevYear_rest = [], thisYear_na = [], thisYear_eu = [],
            thisYear_rest = [];

    for(var i=0; i < qDataPrevYear.length;i++){
        labels.push("Q" + (i+1));
    }

    for(var i=0 ; i < qDataPrevYear.length;i++){
        prevYear_na.push(qDataPrevYear[i].na);
        prevYear_eu.push(qDataPrevYear[i].eu);
        prevYear_rest.push(qDataPrevYear[i].rest);

        thisYear_na.push(qDataThisYear[i].na);
        thisYear_eu.push(qDataThisYear[i].eu);
        thisYear_rest.push(qDataThisYear[i].rest);
    }

    var quarterlyComparisonSeries = [ { name: previousYear + ' REST', data: prevYear_rest, stack: 'forecast' },
                                      { name: previousYear + ' EU', data: prevYear_eu, stack: 'forecast' },
                                      { name: previousYear + ' NA', data: prevYear_na, stack: 'forecast' },
                                      { name: year + ' REST', data: thisYear_rest, stack: 'actual' },
                                      { name: year + ' EU', data: thisYear_eu, stack: 'actual'},
                                      { name: year + ' NA', data: thisYear_na, stack: 'actual'}];
    $(function () {
        $('#quarterlyComparison').highcharts(getStackedBarChartOptions('Quarterly Comparison', previousYear + ' vs '
                                                                + year, labels, leadType, quarterlyComparisonSeries ));
    });

    //Yearly Comparison
    var yearlyLabels = ["Total", "NA", "EU", "Rest", "Unclassified"], yearlyComparisonData_This = [],
        yearlyComparisonData_Previous = [];

    yearlyComparisonData_This.push(yearlyData.thisYear[0].total_users);
    yearlyComparisonData_This.push(yearlyData.thisYear[0].na_users);
    yearlyComparisonData_This.push(yearlyData.thisYear[0].eu_users);
    yearlyComparisonData_This.push(yearlyData.thisYear[0].row_users);
    yearlyComparisonData_This.push(yearlyData.thisYear[0].unclassified_users);

    yearlyComparisonData_Previous.push(yearlyData.prevYear[0].total_users);
    yearlyComparisonData_Previous.push(yearlyData.prevYear[0].na_users);
    yearlyComparisonData_Previous.push(yearlyData.prevYear[0].eu_users);
    yearlyComparisonData_Previous.push(yearlyData.prevYear[0].row_users);
    yearlyComparisonData_Previous.push(yearlyData.prevYear[0].unclassified_users);

    var yearlySeries = [{ name: '2014', data: yearlyComparisonData_Previous },
                        { name: '2015', data: yearlyComparisonData_This }];

    $(function () {
       $('#yearlyComparison').highcharts(getColumnChartOptions('Yearly Comparison',yearlyLabels,leadType,yearlySeries));
    });

    //Forecast vs Cumulative
    var forecastTotal = parseInt(forecastQ.total);
    var forecastNA = parseInt(forecastQ.na);
    var forecastEU = parseInt(forecastQ.eu);
    var forecastREST = parseInt(forecastQ.rest);

    //Total
    $(function () {
        $('#forecastVsCumulativeComparison_Total').highcharts(getForecastVsCumulativeChartOptions(quarter,leadType,
                                                                            cumulativeQ.total,forecastTotal,'Total'));
    });

    //NA
    $(function () {
        $('#forecastVsCumulativeComparison_NA').highcharts(getForecastVsCumulativeChartOptions(quarter,leadType,
                                                                                    cumulativeQ.na,forecastNA,'NA'));
    });

    //EU
    $(function () {
        $('#forecastVsCumulativeComparison_EU').highcharts(getForecastVsCumulativeChartOptions(quarter,leadType,
                                                                                    cumulativeQ.eu,forecastEU,'EU'));
    });

    //REST
    $(function () {
        $('#forecastVsCumulativeComparison_REST').highcharts(getForecastVsCumulativeChartOptions(quarter,leadType,
                                                                            cumulativeQ.rest,forecastREST,'REST'));
    });
};

var getForecastVsCumulativeChartOptions = function (quarter,leadType,cumulativeQ,forecastQ,regionType) {

    return {
        title: {
            text: 'Forecast vs Cumulative for ' + regionType + ' ' + leadType
        },
        subtitle: {
            text: 'Q' + quarter
        },
        xAxis: {
            categories: [' ', ' ',regionType,' ', ' ']
        },
        yAxis: {
            min: 0,
            title: {
                text: leadType
            }
        },
        credits: {
            enabled: false
        },
        tooltip: {
            useHTML: true,
            pointFormat: '<span style="color:{series.color}">\u25CF</span> {series.name}: <b>{point.y}</b><br/>'
        },
        labels: {
            items: [{
                style: {
                    left: '50px',
                    top: '18px',
                    color: (Highcharts.theme && Highcharts.theme.textColor) || 'black'
                }
            }]
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'top',
            x: -5,
            y: 50,
            floating: true,
            borderWidth: 1,
            backgroundColor: ((Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'),
            shadow: true
        },
        series: [{
            type: 'column',
            name: 'Actual',
            data: [0,0,cumulativeQ,0,0]
        }, {
            type: 'line',
            name: 'Forecast',
            data: [forecastQ,forecastQ,forecastQ,forecastQ,forecastQ],
            marker: {
                lineWidth: 2,
                lineColor: Highcharts.getOptions().colors[3],
                fillColor: 'white'
            }
        }]
    };
};

var getStackedBarChartOptions = function(title,subtitle,categories,leadType,series){

    return {

        chart: {
            type: 'column'
        },

        title: {
            text: title
        },
        subtitle: {
            text: subtitle
        },

        xAxis: {
            categories: categories
        },

        yAxis: {
            allowDecimals: false,
            min: 0,
            title: {
                text: leadType
            }
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'top',
            x: -40,
            y: 100,
            floating: true,
            borderWidth: 1,
            backgroundColor: ((Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'),
            shadow: true
        },
        tooltip: {
            formatter: function () {
                return '<b>' + this.x + '</b><br/>' +
                    this.series.name + ': ' + this.y + '<br/>' +
                    'Total: ' + this.point.stackTotal;
            }
        },

        plotOptions: {
            column: {
                stacking: 'normal',
                dataLabels: {
                    enabled: true
                }
            }
        },

        credits: {
            enabled: false
        },

        series: series
    }
};

var getLineChartOptions = function(title,subtitle,leadType,categories,series){

    return {
        chart: {
            type: 'spline'
        },
        title: {
            text: title,
            x: -20 //center
        },
        subtitle: {
            text: subtitle,
            x: -20
        },
        xAxis: {
            categories: categories
        },
        yAxis: {
            title: {
                text: leadType
            },
            min : 0,
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            pointFormat: '<span style="color:{series.color}">\u25CF</span> {series.name}: <b>{point.y}</b><br/>'
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'top',
            x: -5,
            y: 50,
            floating: true,
            borderWidth: 1,
            backgroundColor: ((Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'),
            shadow: true
        },
        credits: {
            enabled: false
        },
        series: series
    };
};

var getColumnChartOptions = function(title,categories,leadType,series){

    return {
        chart: {
            type: 'column'
        },
        title: {
            text: title
        },
        xAxis: {
            categories: categories,
            crosshair: true
        },
        yAxis: {
            min: 0,
            title: {
                text: leadType
            }
        },
        credits: {
            enabled: false
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'top',
            x: -5,
            y: 50,
            floating: true,
            borderWidth: 1,
            backgroundColor: ((Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'),
            shadow: true
        },
        tooltip: {
            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
            '<td style="padding:0"><b>{point.y:.1f}</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
        },
        plotOptions: {
            column: {
                pointPadding: 0.2,
                borderWidth: 0
            }
        },
        series: series
    }
};