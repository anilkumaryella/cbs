package com.test.cbstest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import generated.PayResponse;
import generated.PayTxn;

@Service
public class CbsService {

	@Inject
	private CbsAccountRepo cbsAccountRepo;

	private static final Logger LOGGER = LoggerFactory.getLogger(CbsService.class);

	public List<CbsAccount> getCbsAccount() throws DataException {

		try {
			List<CbsAccount> list = cbsAccountRepo.findAllByStatus("A");
			if (list == null) {
				throw new DataException("01", "NO Account Exists !");
			}
			LOGGER.info("list from DB");
			return list;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			if (e instanceof DataException) {
				throw e;
			}
			throw new DataException("11", "FAILURE");
		}

	}

	public Map<String, String> getBalance(String accountNo) throws DataException {

		try {
			Map<String, String> map = new HashMap<String, String>();

			String balance = cbsAccountRepo.getBalance(accountNo);
			if (balance == null) {
				throw new DataException("01", "No Account Exists !");
			}
			map.put("Account", accountNo);
			map.put("Balance", balance);

			return map;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			if (e instanceof DataException) {
				throw e;
			}
			throw new DataException("11", "FAILURE");
		}

	}

	public PayResponse processPay(PayTxn payTxn) {

		PayResponse response = new PayResponse();
		response.setErrorCode("11");
		response.setErrorMsg("FAILURE");
		try {

			CbsAccount payer = cbsAccountRepo.findOneByAccountNoAndMobileNoAndStatus(payTxn.getDebit().getAccNumber(),
					payTxn.getDebit().getMobile(), "A");
			CbsAccount payee = cbsAccountRepo.findOneByAccountNoAndMobileNoAndStatus(payTxn.getCredit().getAccNumber(),
					payTxn.getCredit().getMobile(), "A");

			if (Float.parseFloat(payer.getBal()) - Float.parseFloat(payTxn.getDebit().getAmount()) < 0.0) {

				response.setErrorCode("06");
				response.setErrorMsg("Insufficient Funds !!");
				return response;
			}
			payer.setBal(
					String.valueOf(Float.parseFloat(payer.getBal()) - Float.parseFloat(payTxn.getDebit().getAmount())));

			payee.setBal(
					String.valueOf(Float.parseFloat(payee.getBal()) + Float.parseFloat(payTxn.getDebit().getAmount())));

			cbsAccountRepo.saveAndFlush(payer);
			cbsAccountRepo.saveAndFlush(payee);
			response.setErrorCode("00");
			response.setErrorMsg("SUCCESS");
		} catch (Exception e) {
			e.getStackTrace();
			response.setErrorCode("01");
			response.setErrorMsg("FAILURE/INVALID DETAILS");
		}
		// LOGGER.info("CBS reqpay Response [{}]", response);
		return response;

	}

}
