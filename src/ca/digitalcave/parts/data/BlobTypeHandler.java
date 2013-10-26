package ca.digitalcave.parts.data;

import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class BlobTypeHandler implements TypeHandler<Blob> {

	@Override
	public Blob getResult(ResultSet rs, String col) throws SQLException {
		return rs.getBlob(col);
	}

	@Override
	public Blob getResult(ResultSet rs, int col) throws SQLException {
		return rs.getBlob(col);
	}

	@Override
	public Blob getResult(CallableStatement cs, int col) throws SQLException {
		return cs.getBlob(col);
	}

	@Override
	public void setParameter(PreparedStatement ps, int col, Blob param, JdbcType type) throws SQLException {
		ps.setBlob(col, param);
	}

}
