package ca.digitalcave.parts.data;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.ResultHandler;

import ca.digitalcave.parts.model.Attribute;
import ca.digitalcave.parts.model.Category;
import ca.digitalcave.parts.model.Part;

public interface PartsMapper {
	List<Category> selectCategories(@Param("account") int account, @Param("terms") List<String> terms, @Param("required") boolean required);
	
	List<Category> selectDigikey(@Param("account") int account, @Param("category") String category, @Param("family") String family);
	
	int insertCategory(@Param("account") int account, @Param("category") Category category);
	int updateCategory(@Param("account") int account, @Param("id") int id, @Param("name") String name);
	int moveCategory(@Param("account") int account, @Param("id") int id, @Param("parent") Integer parent);
	
	int deleteCategory(@Param("account") int account, @Param("id") int id);
	
	void selectParts(@Param("account") int account, @Param("category") Integer category, @Param("terms") List<String> terms, @Param("required") boolean required, ResultHandler handler);
	int insertPart(@Param("account") int account, @Param("part") Part part);
	int updatePart(@Param("account") int account, @Param("part") Part part);
	int deletePart(@Param("account") int account, @Param("id") long id);
	
	void selectAttributes(@Param("account") int account, @Param("part") int part, ResultHandler handler);
	int insertAttribute(@Param("account") int account, @Param("attribute") Attribute attribute);
	int updateAttribute(@Param("account") int account, @Param("attribute") Attribute attribute);
	int deleteAttribute(@Param("account") int account, @Param("id") int id);
	
	Attribute selectAttribute(@Param("account") int account, @Param("attribute") long attribute);

}
