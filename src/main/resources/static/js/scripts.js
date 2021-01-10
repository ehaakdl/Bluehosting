/*!
    * Start Bootstrap - Freelancer v6.0.4 (https://startbootstrap.com/themes/freelancer)
    * Copyright 2013-2020 Start Bootstrap
    * Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-freelancer/blob/master/LICENSE)
    */
(function ($) {
    "use strict";
    let modalFlag = 0;
    $('a.js-scroll-trigger[href*="#"]:not([href="#"])').click(function () {
        if (location.pathname.replace(/^\//, '') == this.pathname.replace(/^\//, '') && location.hostname == this.hostname) {
            var target = $(this.hash);
            target = target.length ? target : $('[name=' + this.hash.slice(1) + ']');
            if (target.length) {
                $('html, body').animate({
                    scrollTop: (target.offset().top - 71)
                }, 1000, "easeInOutExpo");
                return false;
            }
        }
    });


    $(document).scroll(function () {
        var scrollDistance = $(this).scrollTop();
        if (scrollDistance > 100) {
            $('.scroll-to-top').fadeIn();
        } else {
            $('.scroll-to-top').fadeOut();
        }
    });

    // Closes responsive menu when a scroll trigger link is clicked
    $('.js-scroll-trigger').click(function () {
        $('.navbar-collapse').collapse('hide');
    });
    $('#loginMenu').click(function () {
        $('#loginModal').modal();
    });
    $('#signUpMenu').click(function () {
        $('#signupModal').modal();
    });


    // Activate scrollspy to add active class to navbar items on scroll
    $('body').scrollspy({
        target: '#mainNav',
        offset: 80
    });

    // Collapse Navbar
    var navbarCollapse = function () {
        if ($("#mainNav").offset().top > 100) {
            $("#mainNav").addClass("navbar-shrink");
        } else {
            $("#mainNav").removeClass("navbar-shrink");
        }
    };
    // Collapse now if page is not at top
    navbarCollapse();
    // Collapse the navbar when page is scrolled
    $(window).scroll(navbarCollapse);

    // Floating label headings for the contact form
    $(function () {
        $("body").on("input propertychange", ".floating-label-form-group", function (e) {
            $(this).toggleClass("floating-label-form-group-with-value", !!$(e.target).val());
        }).on("focus", ".floating-label-form-group", function () {
            $(this).addClass("floating-label-form-group-with-focus");
        }).on("blur", ".floating-label-form-group", function () {
            $(this).removeClass("floating-label-form-group-with-focus");
        });
    });

    //modal 바깥 영역 클릭시 hide
    $(document).on("click", function (e) {
        if ($(".modal-backdrop").is(e.target)) {
            $('.modal').each(function (index, item) {
                $(item).modal("hide");
            })
        }
    });

    function getCookie(cname) {
        var name = cname + "=";
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i].trim();
            if (c.indexOf(name) == 0) return c.substring(name.length, c.length);
        }
        return "";
    }


    function msg_timer() {
        $("#timeMsg").text(expireTime);
        if (expireTime <= 0) {
            clearInterval(timerId);
            $("#emailReq").show();
            $("#codeSend").hide();
            $("#timeMsg").hide();
            $("#stateMsg").hide();
            return;
        }
        expireTime--;
    }

    $("#codeSend").click(function () {
        let xmlHttpReq = new XMLHttpRequest();
        let emailForm = JSON.stringify({
            code: $('#codeInput').val(),
            email: $('#emailInput').val()
        });
        xmlHttpReq.onreadystatechange = function () {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                $("#emailReq").hide();
                $("#codeSend").hide();
                $("#stateMsg").show();
                $("#stateMsg").text("인증성공");
                clearInterval(timerId);
                $("#timeMsg").hide();
            } else if (this.readyState === XMLHttpRequest.DONE) {
                $("#stateMsg").show();
                $("#stateMsg").text("인증실패");
            }
        }
        xmlHttpReq.open('POST', '/email/code/check');
        xmlHttpReq.setRequestHeader('Content-type', 'application/json');
        xmlHttpReq.send(emailForm);
    })

    var expireTime = 180; //초단위
    var timerId;

    $("#emailReq").click(function () {
        let xmlHttpReq = new XMLHttpRequest();
        let email = $('#emailInput').val();
        xmlHttpReq.onreadystatechange = function () {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                $("#emailReq").hide();
                $("#codeSend").show();
                $("#timeMsg").show();
                $("#stateMsg").hide();
            } else if (this.readyState === XMLHttpRequest.DONE) {
                $("#emailReq").show();
                $("#codeSend").hide();
                $("#timeMsg").hide();
                $("#stateMsg").show();
                $("#stateMsg").text("인증실패");
                clearInterval(timerId);
            }
        }
        xmlHttpReq.open('POST', '/email/code/request');
        xmlHttpReq.setRequestHeader('Content-type', 'application/json');
        xmlHttpReq.send(email);
        expireTime = 180
        timerId = setInterval(msg_timer, 1000);
    })

    $("#signupBtn").click(function () {
        let xmlHttpReq = new XMLHttpRequest();
        let accountInfo = JSON.stringify($('#signupForm').serializeObject());
        xmlHttpReq.onreadystatechange = function () {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                window.location.href = "http://localhost";
                $('.result-msg').text("");
            } else if (this.readyState === XMLHttpRequest.DONE && this.status === 502) {
                $('.result-msg').text("아이디 중복");
            } else if (this.readyState === XMLHttpRequest.DONE && this.status === 506) {
                $('#signup-result-msg').text("회원가입 실패");
            }
        }
        xmlHttpReq.open('POST', '/account/signup');
        xmlHttpReq.setRequestHeader('Content-type', 'application/json');
        xmlHttpReq.send(accountInfo);
    });


    $("#logoutMenu").click(function () {
        let xmlHttpReq = new XMLHttpRequest();
        xmlHttpReq.onreadystatechange = function () {
            if (this.readyState === XMLHttpRequest.DONE) {
                window.location.href = "http://localhost";
            }
        }
        xmlHttpReq.open('GET', '/account/logout');
        xmlHttpReq.send();

    });

    $("#loginBtn").click(function () {
        let xmlHttpReq = new XMLHttpRequest();
        let accountInfo = JSON.stringify($('#loginForm').serializeObject());
        xmlHttpReq.onreadystatechange = function () {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                window.location.href = "http://localhost";
            } else if(this.readyState === XMLHttpRequest.DONE){
                $('#login-result-msg').text("로그인 실패");
            }
        }
        xmlHttpReq.open('POST', '/account/login');
        xmlHttpReq.setRequestHeader('Content-type', 'application/json');
        xmlHttpReq.send(accountInfo);
    });
})(jQuery); // End of use strict
  