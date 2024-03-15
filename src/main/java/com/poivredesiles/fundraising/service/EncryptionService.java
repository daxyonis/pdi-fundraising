package com.poivredesiles.fundraising.service;

import com.poivredesiles.fundraising.config.properties.ApplicationProperties;
import com.poivredesiles.fundraising.converter.StringCryptoConverter;
import com.poivredesiles.fundraising.model.business.BusinessNumber;
import com.poivredesiles.fundraising.model.business.BusinessNumberTypeEnum;
import com.poivredesiles.fundraising.model.group.PdiCampaign;
import com.poivredesiles.fundraising.repository.business.BusinessNumberRepository;
import com.poivredesiles.fundraising.repository.group.PdiCampaignRepository;
import com.poivredesiles.fundraising.repository.group.PdiSellerRepository;
import com.poivredesiles.fundraising.repository.order.OrderHeaderRepository;
import com.poivredesiles.fundraising.repository.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EncryptionService {

    private static final String CIPHER = "AES";
    private PdiCampaignRepository pdiCampaignRepository;

    private PdiSellerRepository pdiSellerRepository;

    private OrderHeaderRepository orderHeaderRepository;

    private UserRepository userRepository;

    private BusinessNumberRepository businessNumberRepository;

    private ApplicationProperties applicationProperties;

    private StringCryptoConverter stringCryptoConverter;

    private final Logger log = LoggerFactory.getLogger(EncryptionService.class);

    public EncryptionService (PdiCampaignRepository pdiCampaignRepository,
                              PdiSellerRepository pdiSellerRepository,
                              OrderHeaderRepository orderHeaderRepository,
                              UserRepository userRepository,
                              BusinessNumberRepository businessNumberRepository,
                              ApplicationProperties applicationProperties,
                              StringCryptoConverter stringCryptoConverter) {
        this.pdiCampaignRepository = pdiCampaignRepository;
        this.pdiSellerRepository = pdiSellerRepository;
        this.orderHeaderRepository = orderHeaderRepository;
        this.userRepository = userRepository;
        this.businessNumberRepository = businessNumberRepository;
        this.applicationProperties = applicationProperties;
        this.stringCryptoConverter = stringCryptoConverter;
    }


    public void batchEncrypt() {
        checkDbEncryptionState();
        if (applicationProperties.getAction().isEncrypt() && !applicationProperties.isEncrypted()) {
            log.info("Encrypting DB ===========>");
            encryptAll();
            changeDbEncryptionState(true);
            log.info("<========== DB encrypted");
        }
    }

    private void checkDbEncryptionState() {
        log.info("Checking DB state");
        Optional<BusinessNumber> encryptionState = businessNumberRepository.findById(BusinessNumberTypeEnum.ENCRYPTED);
        applicationProperties.setEncrypted(encryptionState.get().getNumber() > 0);
    }

    private void changeDbEncryptionState(boolean state) {
        Optional<BusinessNumber> encryptionState = businessNumberRepository.findById(BusinessNumberTypeEnum.ENCRYPTED);
        if (encryptionState.isPresent()) {
            encryptionState.get().setNumber(state ? 1L : 0L);
            businessNumberRepository.save(encryptionState.get());
            applicationProperties.setEncrypted(state);
        }
    }

    /**
     * Encrypt all sensitive data : only load and save will do it automatically
     */
    public void encryptAll() {
        log.info("------------------- Encrypting all sensitive data -------------------");
        encryptPdiCampaigns();
        encryptPdiSellers();
        encryptOrderHeaders();
        encryptUsers();
    }

    private void encryptPdiCampaigns() {
        log.info("-----------> Encrypting campaigns");
        List<PdiCampaign> campaigns = pdiCampaignRepository.findAll();
        for( PdiCampaign campaign : campaigns) {
            // Encrypt sensitive data
            String leaderEmail = campaign.getLeaderEmail();
            campaign.setLeaderEmail(stringCryptoConverter.convertToDatabaseColumn(leaderEmail));
            pdiCampaignRepository.save(campaign);
        }
    }

    private void encryptPdiSellers() {
        log.info("-----------> Encrypting sellers");
        pdiSellerRepository.findAll().forEach(seller -> {
            // Encrypt sensitive data
            String name = seller.getName();
            seller.setName(stringCryptoConverter.convertToDatabaseColumn(name));
            pdiSellerRepository.save(seller);
        });
    }

    private void encryptOrderHeaders() {
        log.info("-----------> Encrypting order headers");
        orderHeaderRepository.findAll().forEach(orderHeader -> {
            // Encrypt sensitive data
            String name = orderHeader.getBuyerName();
            String email = orderHeader.getBuyerEmail();
            String phone = orderHeader.getBuyerPhone();
            orderHeader.setBuyerName(stringCryptoConverter.convertToDatabaseColumn(name));
            orderHeader.setBuyerEmail(stringCryptoConverter.convertToDatabaseColumn(email));
            orderHeader.setBuyerPhone(stringCryptoConverter.convertToDatabaseColumn(phone));
            orderHeaderRepository.save(orderHeader);
        });
    }

    private void encryptUsers() {
        log.info("-----------> Encrypting users");
        userRepository.findAll().forEach(user -> {
            // Encrypt sensitive data
            String firstName = user.getFirstname();
            String lastName = user.getLastname();
            user.setFirstname(stringCryptoConverter.convertToDatabaseColumn(firstName));
            user.setLastname(stringCryptoConverter.convertToDatabaseColumn(lastName));
            userRepository.save(user);
        });
    }


}
