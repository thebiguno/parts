package ca.digitalcave.parts.data;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.ResultHandler;

import ca.digitalcave.parts.model.Account;
import ca.digitalcave.parts.model.Attachment;
import ca.digitalcave.parts.model.Attribute;
import ca.digitalcave.parts.model.Category;
import ca.digitalcave.parts.model.Family;
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

	void selectCategories(ResultHandler handler);
	void insertCategory(@Param("category") Category category);
	void updateCategory(@Param("category") Category category);
	void deleteCategory(@Param("id") int id);
	
	void selectFamilies(@Param("category") int category, ResultHandler handler);
	void insertFamily(@Param("family") Family family);
	void updateFamily(@Param("family") Family family);
	void deleteFamily(@Param("id") int id);
	
	void selectParts(@Param("category") int category, @Param("family") int family, ResultHandler handler);
	void insertPart(@Param("part") Part part, @Param("account") Account account);
	void updatePart(@Param("part") Part part);
	void deletePart(@Param("id") int id);
	
	void selectAttributes(@Param("part") int part, ResultHandler handler);
	void insertAttribute(@Param("attribute") Attribute attribute);
	void updateAttribute(@Param("attribute") Attribute attribute);
	void deleteAtttribute(@Param("partId") int partId, @Param("name") String name);
	
	void selectAttachments(@Param("part") int part, ResultHandler handler);
	void selectAttachment(@Param("attachment") int attachment, ResultHandler handler);
	void insertAttachment(@Param("attachment") Attachment attachment);
	void deleteAttachment(@Param("attachment") int attachment);
	
	Account selectAccount(String identifier);
}
