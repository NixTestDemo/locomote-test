(function () {
    'use strict';

    var
        URL = 'http://localhost:8080/search',
        data = {},
        activeTab = 'tab-table-3',
        currentPage = 1,
        itemsPerPage = 20,
        loader = $('.loader'),
        results = $('#results'),
        fromLocation = $('#from'),
        toLocation = $('#to'),
        travelingDate = $('#date');

    $('.datepicker').datepicker({
        format: 'yyyy-mm-dd',
        startDate: new Date()
    });

    $('#flyForm').submit(function (event) {
        data = {};
        event.preventDefault();
        loader.show();
        results.hide();
        currentPage = 1;
        $.get(URL, {
            from: fromLocation.val(),
            to: toLocation.val(),
            date: travelingDate.val()
        }).done(function (response) {
            if (!$.isEmptyObject(response)) {
                data = response;
                parseResult(data);
                results.show();
            }
        }).fail(function () {
            results.hide();
            alert('Something went wrong!');
        }).always(function () {
            loader.hide();
        });
    });

    $('#clearBtn').click(function () {
        fromLocation.val('');
        toLocation.val('');
        travelingDate.val('');
    });

    $('#data-tabs').click(function (event) {
        event.preventDefault();
        activeTab = event.target.id;
        currentPage = 1;
        renderTable(activeTab);
    });

    function parseResult(data) {
        var tableNumber = 0;
        for (var key in data) {
            tableNumber++;
            $('#tab-table-' + tableNumber).text(key);
        }
        renderTable('tab-table-3');
    }

    function pagination(data) {
        var pagination =
            '<nav aria-label="Page navigation"><ul class="pagination">';
        var pages = Math.ceil(data.length / itemsPerPage);
        for (var i = 1; i <= pages; i++) {
            pagination += '<li page="' + i + '"><a href="" aria-label="' + i + '">' + i + '</a></li>'
        }
        pagination += '</ul></nav>';
        return pagination;
    }

    function changePage(page) {
        if (currentPage === parseInt(page)) {
            return;
        }
        currentPage = parseInt(page);
        renderTable(activeTab);
    }

    function addPagination() {
        $('.pagination').click(function (event) {
            event.preventDefault();
            changePage($(event.target).attr('aria-label'));
        });
    }

    function renderTable(table) {
        var result = '<table class="table"><thead><tr><th>Fly number</th><th>Airline</th><th>Duration</th><th>From</th><th>To</th><th>Price</th></tr></thead></thead><tbody>';
        var flightsInfo = data[$('#' + table).html()];
        if (flightsInfo.length) {
            var startItem = currentPage * itemsPerPage - itemsPerPage;
            var pageLength = flightsInfo.length - (currentPage - 1) * itemsPerPage > itemsPerPage ? itemsPerPage : flightsInfo.length - (currentPage - 1) * itemsPerPage;
            for (var i = startItem; i < startItem + pageLength; i++) {
                result += '<tr>';
                result += '<td>' + flightsInfo[i].flightNum + '</td>';
                result += '<td>' + flightsInfo[i].airline.code + ', ' + flightsInfo[i].airline.name + '</td>';
                result += '<td>' + Math.floor(flightsInfo[i].durationMin / 60) + 'h, ' + flightsInfo[i].durationMin % 60 + 'm' + '</td>';
                result += '<td>' + flightsInfo[i].start.countryName + ', ' + flightsInfo[i].start.airportName + '</td>';
                result += '<td>' + flightsInfo[i].finish.countryName + ', ' + flightsInfo[i].finish.airportName + '</td>';
                result += '<td>' + flightsInfo[i].price + '</td>';
                result += '</tr>';
            }
            result += '</tbody></table>';
            result += pagination(flightsInfo);
        } else {
            result = '<div class="row"><div class="col-md-12 text-center"><h4 class="input">No flights found!</h4></div></div>'
        }
        $('#table-' + activeTab.split('-')[2]).html(result);
        $('#table-' + activeTab.split('-')[2] + ' .pagination [page="' + currentPage + '"]').addClass('active');
        addPagination();
    }

})();
