package com.poivredesiles.fundraising.resource;

import java.math.BigInteger;

public record ChargeRequest(BigInteger amount,
                            String currency,
                            String description,
                            String external_reference_id,
                            String receipt_email,
                            String source){
}
