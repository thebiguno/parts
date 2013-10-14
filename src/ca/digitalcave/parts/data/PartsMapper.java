package ca.digitalcave.parts.data;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ca.digitalcave.parts.model.Account;
import ca.digitalcave.parts.model.Attribute;
import ca.digitalcave.parts.model.Category;
import ca.digitalcave.parts.model.Part;

public interface PartsMapper {
	/**
	 * Search for a tree of part categories and families and the number of parts that match the search terms.
	 */
	List<Category> search(@Param("terms") List<String> terms);
	
	/**
	 * Get a list of attribute names by family.
	 */
	List<String> attributesByFamily(@Param("category") String category, @Param("family") String family);

	/**
	 * Get a list of parts by family.
	 */
	List<Part> partsByFamily(@Param("category") String category, @Param("family") String family);
	
	/**
	 * Get a list of attributes for a part.
	 */
	List<Attribute> attributesByPart(@Param("partId") int partId);
	
	short newPartId();
	void insert(@Param("attribute") Attribute attribute);
	void remove(@Param("partId") int partId);
	void setQuantity(@Param("partId") int partId, @Param("value") String value);

	
	Account selectAccount(String identifier);
}
