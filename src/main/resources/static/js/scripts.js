/*!
    * Start Bootstrap - Freelancer v6.0.4 (https://startbootstrap.com/themes/freelancer)
    * Copyright 2013-2020 Start Bootstrap
    * Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-freelancer/blob/master/LICENSE)
    */
(function ($) {
    "use strict"; // Start of use strict
    let modalFlag = 0;
    // Smooth scrolling using jQuery easing
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

    // Scroll to top button appear
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




    //회원가입 정보 서버로 송수신
    $("#loginBtn").click(function () {
        /*
        let accountInfo = JSON.stringify($('#loginForm').serializeObject());
        $.ajax({
            url: "/account/login",
            headers: {
                'Content-Type':'application/json'
            },
            method: 'POST',
            cache: false,
            dataType: 'json',
            data: accountInfo,
            success: function(data){
                window.location.href = data.redirect;
            },
            error: function (request, status, error){
                console.log("로그인 실패");

            }
        });
        */

        let xmlHttpReq = new XMLHttpRequest();
        let accountInfo = JSON.stringify($('#loginForm').serializeObject());
        xmlHttpReq.onreadystatechange = function () {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                document.cookie();

                window.location.href = "http://localhost";
            } else {
                cosole.log("로그인 실패");
            }
        }


        xmlHttpReq.open('POST', '/account/login');
        xmlHttpReq.setRequestHeader('Content-type', 'application/json');
        xmlHttpReq.send(accountInfo);



    });
})(jQuery); // End of use strict
  