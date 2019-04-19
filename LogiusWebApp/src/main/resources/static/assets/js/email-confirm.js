function getActivateToken() {
    var urlParams = new URLSearchParams(window.location.search);
    console.log(urlParams.get('token'));
    return urlParams.get('token');
}

$(document).ready(function () {
    function activateAccount() {
        $.ajax({
            url: "/api/user/email-confirm",
            type: "POST",
            headers: {Authorization: 'Bearer ' + getActivateToken()},
            success: function (accountInfo) {
                localStorage.setItem('token', accountInfo['token']);
                $(location).attr('href', '/index.html')
            },
            error: function (error) {
                localStorage.removeItem('token');
                $(location).attr('href', '/index.html')
            }
        });
    }

    activateAccount();
});
