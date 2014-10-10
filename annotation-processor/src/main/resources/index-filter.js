(function($) {
    "use strict";

    $(function() {
        var filter = $("#filter");
        var headings = $("h2");
        var tables = $("table");
        var rows = $("tr[data-filtered]");

        filter.focus();

        filter.keyup(_.debounce(function() {
            headings.hide();
            rows.hide();

            var filterText = $.trim($(this).val()).toLowerCase();

            // this is really stupidly inefficient, but let's solve that once the need comes
            rows.filter(function() {
                return $(this).text().toLowerCase().search(filterText) >= 0;
            }).show();

            tables.filter(function() {
                return $("tr:visible", $(this)).length;
            }).each(function() {
                $(this).prev("h2").show();
            });
        }, 100));
    });
})(jQuery);
