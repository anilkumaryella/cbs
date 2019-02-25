package com.test.cbstest;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import generated.PayResponse;
import generated.PayTxn;

@RestController
@RequestMapping(value = "/app/cbs/", consumes = Versions.V1_0, produces = Versions.V1_0)

public class CbsController {

	@Inject
	private CbsService cbsService;

	private static final Logger LOGGER = LoggerFactory.getLogger(CbsController.class);

	@RequestMapping(value = "/listallacc", method = RequestMethod.GET)
	public List<CbsAccount> listacc() throws DataException {
		LOGGER.info("CBS listallacc with status A");
		return cbsService.getCbsAccount();
	}

	@RequestMapping(value = "/balenq", method = RequestMethod.GET)
	public Map<String, String> balenq(@RequestParam String accNo) throws DataException {
		LOGGER.info("CBS balenq accNo [{}] ", accNo);
		return cbsService.getBalance(accNo);

	}

	@RequestMapping(value = "/reqpay", method = RequestMethod.POST, consumes = Versions.V2_0, produces = Versions.V2_0)
	public PayResponse reqpay(@RequestBody PayTxn payTxn) {
		LOGGER.info("CBS reqpay Request [{}] ", payTxn);
		PayResponse response = cbsService.processPay(payTxn);
		LOGGER.info("CBS reqpay Response [{}] ", response);
		return response;
	}

}
