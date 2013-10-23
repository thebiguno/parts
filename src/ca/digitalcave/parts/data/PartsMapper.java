package ca.digitalcave.parts.data;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.ResultHandler;

import ca.digitalcave.parts.model.Account;
import ca.digitalcave.parts.model.Attachment;
import ca.digitalcave.parts.model.Attribute;
import ca.digitalcave.parts.model.Category;
import ca.digitalcave.parts.model.Part;

public interface PartsMapper {
	List<Category> selectCategories(@Param("account") int account, @Param("terms") List<String> terms);
	
	List<Category> selectDigikey(@Param("account") int account, @Param("category") String category, @Param("family") String family);
	
	void insertCategory(@Param("category") Category category);
	void updateCategory(@Param("id") int id, @Param("name") String name);
	void deleteCategory(@Param("id") int id);
	
	void selectParts(@Param("category") Integer category, @Param("family") Integer family, @Param("terms") List<String> terms, ResultHandler handler);
	void insertPart(@Param("part") Part part, @Param("account") Account account);
	void updatePart(@Param("part") Part part);
	void deletePart(@Param("id") int id);
	
	void selectAttributes(@Param("part") int part, ResultHandler handler);
	void insertAttribute(@Param("attribute") Attribute attribute);
	void updateAttribute(@Param("attribute") Attribute attribute);
	void deleteAttribute(@Param("id") int id);
	
	void selectAttachments(@Param("part") int part, ResultHandler handler);
	void selectAttachment(@Param("attachment") int attachment, ResultHandler handler);
	void insertAttachment(@Param("attachment") Attachment attachment);
	void deleteAttachment(@Param("attachment") int attachment);
	
	Account selectAccount(@Param("identifier") String identifier);
}
