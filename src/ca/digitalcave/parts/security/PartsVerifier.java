package ca.digitalcave.parts.security;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.security.Verifier;

import ca.digitalcave.moss.crypto.MossHash;
import ca.digitalcave.parts.PartsApplication;
import ca.digitalcave.parts.data.AccountMapper;
import ca.digitalcave.parts.model.Account;

public class PartsVerifier implements Verifier {

	@Override
	public int verify(Request request, Response response) {
		final ChallengeResponse cr = request.getChallengeResponse();
		if (cr == null) return RESULT_MISSING;

		final PartsApplication application = (PartsApplication) Application.getCurrent();

		final SqlSession sql = application.getSqlFactory().openSession(true);
		try {
			final Map<String, Object> map = sql.getMapper(AccountMapper.class).select(cr.getIdentifier());
			if (map == null) return RESULT_UNKNOWN;
			final Account account = new Account(map);
			cr.setIdentifier(account.getIdentifier()); // the identifier could be an email or activation key so replace it
			
			if (checkSecret(cr, account) == false) return RESULT_INVALID;

			request.getClientInfo().setUser(account);

			return RESULT_VALID;
		} finally {
			sql.close();
		}
	}
	
	private boolean checkSecret(ChallengeResponse cr, Account account) {
		if ((cr.getSecret() == null) || account.getSecret() == null) return false;
			
		if (account.getSecretString().startsWith("SHA")) {
			return MossHash.verify(account.getSecretString(), new String(cr.getSecret()));
		} else {
			return account.getSecretString().equals(new String(cr.getSecret()));
		}
	}
	

}
