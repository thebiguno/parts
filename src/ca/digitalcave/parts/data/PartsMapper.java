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
	
	int insertCategory(@Param("account") int account, @Param("category") Category category);
	int updateCategory(@Param("account") int account, @Param("id") int id, @Param("name") String name);
	int deleteCategory(@Param("account") int account, @Param("id") int id);
	
	void selectParts(@Param("account") int account, @Param("category") Integer category, @Param("terms") List<String> terms, ResultHandler handler);
	int insertPart(@Param("account") int account, @Param("part") Part part);
	int updatePart(@Param("account") int account, @Param("part") Part part);
	int deletePart(@Param("account") int account, @Param("id") int id);
	
	void selectAttributes(@Param("part") int part, ResultHandler handler);
	int insertAttribute(@Param("attribute") Attribute attribute);
	int updateAttribute(@Param("attribute") Attribute attribute);
	int deleteAttribute(@Param("id") int id);
	
	void selectAttachments(@Param("part") int part, ResultHandler handler);
	void selectAttachment(@Param("attachment") int attachment, ResultHandler handler);
	int insertAttachment(@Param("attachment") Attachment attachment);
	int deleteAttachment(@Param("attachment") int attachment);
	
	Account selectAccount(@Param("identifier") String identifier);
}
