package com.test.cbstest;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CbsAccountRepo extends JpaRepository<CbsAccount, CbsAccountPK> {

	List<CbsAccount> findAllByStatus(String status);

	@Query("select C.bal from CbsAccount C where C.accountNo =:accountNo")
	public String getBalance(@Param("accountNo") String accountNo);

	CbsAccount findOneByAccountNoAndMobileNoAndStatus(String accNumber, String mobileNo, String status);

}
