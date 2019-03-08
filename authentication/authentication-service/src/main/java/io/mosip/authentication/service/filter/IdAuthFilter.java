package io.mosip.authentication.service.filter;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.jayway.jsonpath.JsonPath;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class IdAuthFilter.
 *
 * @author Manoj SP
 */
@Component
public class IdAuthFilter extends BaseAuthFilter {
	
	/** The Constant REQUEST. */
	private static final String REQUEST = "request";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#setTxnId(java.util.Map,
	 * java.util.Map)
	 */
	@Override
	protected Map<String, Object> setResponseParams(Map<String, Object> requestBody, Map<String, Object> responseBody)
			throws IdAuthenticationAppException {
		Map<String, Object> responseParams = super.setResponseParams(requestBody, responseBody);
		return setAuthResponseParam(requestBody, responseParams);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#decodedRequest(java.
	 * util.Map)
	 */
	@Override
	protected Map<String, Object> decipherRequest(Map<String, Object> requestBody) throws IdAuthenticationAppException {
		try {
			requestBody.replace(REQUEST, decode((String) requestBody.get(REQUEST)));
			if (null != requestBody.get(REQUEST)) {
				Map<String, Object> request = keyManager.requestData(requestBody, mapper);
				requestBody.replace(REQUEST, request);
			}
			return requestBody;
		} catch (ClassCastException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#validateSignature(java.
	 * lang.String, byte[])
	 */
	@Override
	protected boolean validateSignature(String signature, byte[] requestAsByte) throws IdAuthenticationAppException {
		return true;
	}
	
	private void licenseKeyMISPMapping(String licenseKey,String mispId) throws IdAuthenticationAppException {
		String licensekeyMappingJson=env.getProperty("licensekey.mispmapping."+licenseKey+"."+mispId);
		String expiryDt = JsonPath.read(licensekeyMappingJson, "expiryDt");
		if(DateUtils.convertUTCToLocalDateTime(expiryDt).isBefore(DateUtils.getUTCCurrentDateTime())){
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.MISP_LICENSEKEYEXP);
		}
		
	}

}