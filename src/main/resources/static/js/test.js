$(document).ready(function($) {
    $("#submitbutton").click(function(){
        var xhr = new XMLHttpRequest();
        var data = JSON.stringify($('#signupform').serializeObject());

        xhr.onreadystatechange = function() {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                console.log("success");
                var dataa = this.getAllResponseHeaders();
            }
        }
        xhr.open('POST', '/account/login');
        xhr.setRequestHeader('Content-type', 'application/json'); // 컨텐츠타입을 json으로

        xhr.send(data); // 데이터를 stringify해서 보냄
    });
});
