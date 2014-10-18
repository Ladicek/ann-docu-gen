(function($) {
    "use strict";

    $(function() {
        var search = $("#search");

        var searchData = new Bloodhound({
            datumTokenizer: function(d) {
                return d.tokens;
            },
            queryTokenizer: Bloodhound.tokenizers.whitespace,
            limit: 10,
            prefetch: "search.json"
        });

        searchData.initialize().done(function() {
            search.prop("disabled", false);

            search.typeahead({
                highlight: true
            }, {
                name: "search",
                source: searchData.ttAdapter(),
                displayKey: "name",
                templates: {
                    suggestion: function(suggestion) {
                        return '<p class="search-result-type">' + suggestion.type + '</p>' +
                            '<p class="search-result-name">' + suggestion.name + '</p>' +
                            '<p class="search-result-description">' + suggestion.description + '</p>';
                    }
                }
            });

            search.on("typeahead:selected", function(event, suggestion) {
                window.location.href = suggestion.fqn + ".html";
            });

            search.focus();
        });
    });
})(jQuery);
