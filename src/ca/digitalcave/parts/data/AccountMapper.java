package ca.digitalcave.parts.data;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import ca.digitalcave.parts.model.Account;

public interface AccountMapper {
	
	/**
	 * Select the user where the supplied identifier that matches the identifier, email, or activationKey fields 
	 * @param identifier the identifier, email, activation key
	 */
	Map<String, Object> select(@Param("identifier") String identifier);
	void insert(@Param("account") Account account);
	void updateSecret(@Param("identifier") String identifier, @Param("secret") String secret);
	void updateActivationKey(@Param("identifier") String identifier, @Param("activationKey") String activationKey);
}
