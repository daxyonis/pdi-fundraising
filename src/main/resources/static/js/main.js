// Main JS functions for all pages
"use strict";


function displayDisappear(selector, message) {
	$(selector).text(message);
	$(selector).removeClass("d-none");
	setTimeout(function() {
		$(selector).addClass("d-none");
	}, 5000);
}

function displaySuccess(selector, message) {
	displayDisappear(selector, message);
}

function displayError(jqXHR, selector, message) {
	console.log("Status: " + jqXHR.responseText);
	displayDisappear(selector, message);
}

function displayFailure (err, selector){
    console.log(err.responseJSON);
    if(err.responseJSON && err.responseJSON.message){
        displayError(err, selector, `ERREUR : ${err.responseJSON.message}`);
    } else {
        displayError(err, selector, /*[[#{email.send.error}]]*/'Erreur');
    }
}