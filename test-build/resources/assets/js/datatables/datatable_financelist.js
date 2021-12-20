
$(function() {

	var table = $('#datatable_financelist').DataTable( {
		"scrollX": true,
		'bServerSide': false,
		'ajax': './FinanceListServlet',
		'columns':  [
					{ 'mData': 'userNr' },
					{ 'mData': 'fullName' },
       				{ 'mData': 'fullAddress' },
       				{ 'mData': 'email' },
       				{ 'mData': 'campaignName' },
       				{ 'mData': 'amount' },
       				{ 'mData': 'appFee' },
       				{ 'mData': 'transFee' },
       				{ 'mData': 'orgAmt' },
       				{ 'mData': 'transDate' },
       				{ 'mData': 'paymentMethodName' },
       				{ 'mData': 'transCode' },
       				{ 'mData': 'paymentStatusName' },
       				{ 'mData': 'lastColumn' }
       				],
		'fnServerParams': function ( aoData ) {
            aoData.push(	{ "name": "activeUsersId", "value": $("#activeUsersId").val() },
		            		{ "name": "activeCompanyId", "value": $("#activeCompanyId").val() },
		            		{ "name": "activeLocale", "value": $("#activeLocale").val() },
		            		{ "name": "startDate", "value": $("#startDate").val() },
		            		{ "name": "endDate", "value": $("#endDate").val() }
	            		);
        },
        'bJQueryUI': true,
		dom: 'fiBrtlp',
        buttons: {
        	buttons: [
        		{ 	
        			extend: 'print',
                 	className: 'btn',
                 	text: '<i class="fa fa-print" ></i>',
                 	exportOptions: {
             			columns: ':visible:not(:last-child)'
                 	}
                },
                { 	
                	extend: 'excel', 
                 	className: 'btn',
                 	text: '<i class="fa fa-file-excel-o" ></i>',
                    exportOptions: {
                     	columns: ':visible:not(:last-child)'
                    }
                },
                { 	
                	extend: 'pdf', 
                	className: 'btn',
                 	text: '<i class="fa fa-file-pdf-o" ></i>',
                    exportOptions: {
                     	columns: ':visible:not(:last-child)'
                    },
                    orientation: 'portrait',
	                pageSize: 'A3'
             	},
             	{ 
            	 	extend: 'colvis', 
            	 	className: 'btn',
            	 	text: '<i class="fa fa-bars"></i> <span class="caret"></span>'
            	}
            ]
        },
        columnDefs: [
            {
           	 targets: [2,3],
                visible: false
            }
        ],
        "oLanguage": {
        	"oPaginate": { "sPrevious": '<icon class="fa fa-arrow-left">', "sNext": '<icon class="fa fa-arrow-right">' },
            "sInfo": $("#db_showingentries").val() + " _START_ " + $("#db_to").val() + " _END_ " + $("#db_of").val() + " _TOTAL_",
            "sSearch": $("#db_search").val() + ' : ',
            "sSearchPlaceholder":  $("#db_search").val() + "...",
            "sLengthMenu": $("#db_results").val() + " :  _MENU_",
        },
        "stripeClasses": [],
        order: [[ 0, 'desc' ]],
        "lengthMenu": [10, 25, 50, 100],
        "pageLength": 100, 
    	drawCallback: function () {

    	}
	});
	
});


