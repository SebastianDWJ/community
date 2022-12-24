$(function (){
   $("#topBtn").click(setTop);
   $("#wonderfulBtn").click(setWonderful);
   $("#deleteBtn").click(setDelete);
});
//置顶
function setTop(){
    $.post(
        CONTEXT_PATH+"/discuss/top",
        {"id":$("#discussPostId").val()},
        function (data){
            data = $.parseJSON(data);
            if(data.code==0){
                $("#topBtn").attr("disabled","disabled");
            }else {
                alert(data.msg);
            }
        }
    );
}
//加精
function setWonderful(){
    $.post(
        CONTEXT_PATH+"/discuss/wonderful",
        {"id":$("#discussPostId").val()},
        function (data){
            data = $.parseJSON(data);
            if(data.code==0){
                $("#wonderfulBtn").attr("disabled","disabled");
            }else {
                alert(data.msg);
            }
        }
    );
}
//置顶
function setDelete(){
    $.post(
        CONTEXT_PATH+"/discuss/delete",
        {"id":$("#discussPostId").val()},
        function (data){
            data = $.parseJSON(data);
            if(data.code==0){
                location.href=CONTEXT_PATH+"/index";
            }else {
                alert(data.msg);
            }
        }
    );
}

function like(btn,entityType,entityId,entityUserId,discussPostId){
    $.post(
        CONTEXT_PATH+"/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"discussPostId":discussPostId},
        function (data){
            data = $.parseJSON(data);
            if(data.code==0){
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?"已赞":"点赞");
            }else{
                alert(data.msg);
            }
        }
    );
}

