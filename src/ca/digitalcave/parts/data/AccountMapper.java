package ca.digitalcave.parts.data;

import org.apache.ibatis.annotations.Param;

import ca.digitalcave.parts.model.Account;

public interface AccountMapper {
	
	/**
	 * Select the user where the supplied identifier that matches the identifier, email, or activationKey fields 
	 * @param identifier
	 * @return
	 */
	Account select(@Param("identifier") String identifier);
	void insert(@Param("account") Account account);
	void updateSecret(@Param("account") Account account);
	void updateActivationKey(@Param("account") Account account);
}
