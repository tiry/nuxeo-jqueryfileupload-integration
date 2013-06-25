

jQuery(document).ready(function() {
           var batchId = "batch-" + new Date().getTime() + "-" + Math.floor(Math.random()*1000);
           var automationHeaders = { 'X-Batch-Id' : batchId,
                   'X-File-Idx' : '0'};
           jQuery('head').append('<link rel="stylesheet" href="' + nxContextPath + '/css/jquery.fileupload-ui-noscript.css" type="text/css" />');
           jQuery("input[type='file'][id$=fileupload]").fileupload({ url : nxContextPath + "/site/automation/batch/upload",
             //postMessage : false,
             //singleFileUploads : true,
             multipart : false,
             headers : automationHeaders,
             progress : function (e, data) {
               var targetId = this.id + "_progress";
               if (data.total>0) {
                   var progressdiv = jQuery("#" + targetId);
                   var processPercent = Math.floor(data.loaded *100 / data.total);
                   var speed = Math.floor(data.bitrate / 1024);
                   progressdiv.html( processPercent + "% (" + speed + " KB/s)");
                   var progressBar = jQuery("#" + targetId+"bar");
                   progressBar.css({height : '5px', backgroundColor : 'blue', width : Math.floor(200*data.loaded / data.total)});
               }
             },
             always: function (e, options) {
               if (options.jqXHR.responseText=="uploaded") {
               var targetId = this.id + "_progress";
                 var progressdiv = jQuery("#" + targetId);
                 progressdiv.html("<img src='" + nxContextPath + "/img/standart_waiter.gif'/>");
                 var targetId = this.id + "_operationId";
                   var operationId = jQuery("#" + targetId).val();
                   console.log("exec " + operationId);
                   var op = jQuery().automation(operationId, {automationParams: { params : {}, context : ctx }});
                   op.batchExecute(batchId, function() {
                     window.location.href=window.location.href;
                     });
               } else {
                 alert("Error during import");
                 console.log(e,options);
               }
             }});
});