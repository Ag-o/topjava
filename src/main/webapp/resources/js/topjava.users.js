var ctx;

// $(document).ready(function () {
$(function () {
    // https://stackoverflow.com/a/5064235/548473
    ctx = {
        ajaxUrl: "admin/users/",
        datatableApi: $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "name"
                },
                {
                    "data": "email"
                },
                {
                    "data": "roles"
                },
                {
                    "data": "enabled"
                },
                {
                    "data": "registered"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "asc"
                ]
            ]
        })
    };
    makeEditable();
});

function enableDisable(element) {
    let row = element.closest("tr");
    let id = row.attr("id");
    let enabled = element.is(":checked") ? "true" : "false";
    $.ajax({
        type: "POST",
        url: ctx.ajaxUrl + "enableDisable/" + id,
        data: "enabled=" + enabled
    }).done(function () {
        updateTable();
        if (enabled) {
            successNoty("Enable");
        } else {
            successNoty("Disable")
        }
    });
}
