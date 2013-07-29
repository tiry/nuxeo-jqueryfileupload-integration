

function initJQUpload(cb) {
    var batchId = "batch-" + new Date().getTime() + "-" + Math.floor(Math.random()*1000);
    var automationHeaders = { 'X-Batch-Id' : batchId,
            'X-File-Idx' : '0', 'Nuxeo-Transaction-Timeout' : 2*5*60 };
    jQuery("input[type='file'][id$=fileupload]").fileupload({ url : nxContextPath + "/site/automation/batch/upload",
      singleFileUploads : true,
      multipart : false,
      headers : automationHeaders,
      formData : {'batchId' : batchId, 'fileIdx' : '0'},
      progress : function (e, data) {
        if (data.total>0) {
            var targetId = this.id + "_progressbar_iframe";
            jQuery("#" + targetId).css("display","none");
            targetId = this.id + "_progress";
            var progressdiv = jQuery("#" + targetId);
            var processPercent = Math.floor(data.loaded *100 / data.total);
            var speed = Math.floor(data.bitrate / 1024);
            if (speed>0) {
              progressdiv.html( processPercent + "% (" + speed + " KB/s)");
            }
            var progressBar = jQuery("#" + targetId+"bar");
            progressBar.css({height : '5px', backgroundColor : 'blue', width : Math.floor(200*data.loaded / data.total)});
        }
      },
      send : function (e, data) {
          var targetId = this.id + "_progressbar_iframe";
          jQuery("#" + targetId).css("display","block");
      },
      done : function (e, data) {
          var targetId = this.id + "_progress";
          var opIdContainerId = this.id + "_operationId";
          var operationId = jQuery("#" + opIdContainerId).val();
          console.log("exec " + operationId);
          if (cb) {
              cb(batchId, operationId);
          } else {
              var progressdiv = jQuery("#" + targetId);
              progressdiv.html("<img src='" + nxContextPath + "/img/standart_waiter.gif'/>");
              var op = jQuery().automation(operationId, {automationParams: { params : {}, context : ctx }});
              op.batchExecute(batchId, function() {
                window.location.href=window.location.href;
              });
            }

      },
      fail : function (e, data) {
          alert("Error during import: see js console for more info :" + data.errorThrown);
          console.log(e,data);
      }
      });
}
