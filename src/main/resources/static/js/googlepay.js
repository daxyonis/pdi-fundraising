/**************************/
/*      For Google pay    */  
/**************************/

const baseRequest = {
  apiVersion: 2,
  apiVersionMinor: 0
};

const tokenizationSpecification = {
  type: 'PAYMENT_GATEWAY',
  parameters: {
      "gateway": "globalpayments",
  	  "gatewayMerchantId": "MY_GATEWAY_MERCHANT_ID"
  }
};

const allowedCardNetworks = ["AMEX", "DISCOVER", "INTERAC", "JCB", "MASTERCARD", "VISA"];
const allowedCardAuthMethods = ["PAN_ONLY", "CRYPTOGRAM_3DS"];

const baseCardPaymentMethod = {
  type: 'CARD',
  parameters: {
    allowedAuthMethods: allowedCardAuthMethods,
    allowedCardNetworks: allowedCardNetworks
  }
};

const cardPaymentMethod = Object.assign(
  {tokenizationSpecification: tokenizationSpecification},
  baseCardPaymentMethod
);

// For ready to pay check
const isReadyToPayRequest = Object.assign({}, baseRequest);
isReadyToPayRequest.allowedPaymentMethods = [baseCardPaymentMethod];

// For payment token exchange
const paymentDataRequest = Object.assign({}, baseRequest);
paymentDataRequest.allowedPaymentMethods = [cardPaymentMethod];
paymentDataRequest.transactionInfo = {
  totalPriceStatus: 'FINAL',
  totalPrice: '',
  currencyCode: 'CAD',
  countryCode: 'CA',
  merchantName: 'Poivre des Îles'
};
paymentDataRequest.merchantInfo = {
  merchantName: 'Poivre des Îles',
  merchantId: '12345678901234567890'
};


function googlePayUpdateTotal(totalString){
	let pricePattern = /^[0-9]+(\.[0-9][0-9])?$/;
	if(pricePattern.exec(totalString)){
		paymentDataRequest.transactionInfo.totalPrice = totalString;	
	} else {
		console.log("Invalid total price String !");
	}
}

const paymentsClient = new google.payments.api.PaymentsClient({environment: 'TEST'});

function googlePayLoaded(){	
	paymentsClient.isReadyToPay(isReadyToPayRequest)
    .then(function(response) {
      if (response.result) {
        // add a Google Pay payment button
		const button = paymentsClient.createButton({onClick: () => processGooglePay});
		document.getElementById('order-container').appendChild(button);
      }
    })
    .catch(function(err) {
      // show error in developer console for debugging
      console.error(err);
    });
}

function processGooglePay(){
	paymentsClient.loadPaymentData(paymentDataRequest).then(function(paymentData){
	// if using gateway tokenization, pass this token without modification
	  paymentToken = paymentData.paymentMethodData.tokenizationData.token;
	}).catch(function(err){
	  // show error in developer console for debugging
	  console.error(err);
	});
}