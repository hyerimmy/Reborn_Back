package spring.reborn.domain.jjim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.domain.jjim.model.JjimReq;
import spring.reborn.domain.jjim.model.JjimRes;

import javax.sql.DataSource;

import static javax.swing.UIManager.getInt;
import static javax.swing.UIManager.getString;
import static spring.reborn.config.BaseResponseStatus.DATABASE_ERROR;

@Repository
public class JjimDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    public JjimRes createJjim(JjimReq jjimReq) throws BaseException {
        try {
            String createJjimQuery = "insert into Jjim (storeIdx, userIdx) values (?,?)";
            Object[] createJjimParams = new Object[]{jjimReq.getStoreIdx(), jjimReq.getUserIdx()};
            this.jdbcTemplate.update(createJjimQuery, createJjimParams);

            String postJjimResponseQuery =
                    "select Jjim.jjimIdx,User.userEmail,Store.storeName\n" +
                            "from Jjim, User, Store " +
                            "where Jjim.jjimIdx=last_insert_id() and Jjim.userIdx = User.userIdx and Jjim.storeIdx = Store.storeIdx;";

            Object[] postJjimParams = new Object[]{};

            //queryForObject : DTO 하나 값 반환
            JjimRes jjimRes = this.jdbcTemplate.queryForObject(postJjimResponseQuery,
                    postJjimParams,
                    (rs, rowNum) -> new JjimRes(
                            rs.getInt("jjimIdx"),
                            rs.getString("userEmail"),
                            rs.getString("storeName"))
            );

            return jjimRes;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public JjimRes deleteJjim(JjimReq jjimReq) throws BaseException {
        try {
            System.out.println("Dao 시작");

            String postJjimResponseQuery =
                    "select Jjim.jjimIdx,User.userEmail,Store.storeName\n" +
                            "from Jjim, User, Store " +
                            "where Jjim.jjimIdx=last_insert_id() and Jjim.userIdx = User.userIdx and Jjim.storeIdx = Store.storeIdx;";
            System.out.println("Dao 1");

            Object[] postJjimParams = new Object[]{};
            System.out.println("Dao 2");

            //queryForObject : DTO 하나 값 반환
            JjimRes jjimRes = this.jdbcTemplate.queryForObject(postJjimResponseQuery,
                    postJjimParams,
                    (rs, rowNum) -> new JjimRes(
                            rs.getInt("jjimIdx"),
                            rs.getString("userEmail"),
                            rs.getString("storeName"))
            );

            System.out.println("Dao 3");

            String deleteJjimQuery = "delete from Jjim where storeIdx=? and userIdx=?";
            Object[] createJjimParams = new Object[]{jjimReq.getStoreIdx(), jjimReq.getUserIdx()};
            this.jdbcTemplate.update(deleteJjimQuery, createJjimParams);
            System.out.println("Dao 끝");

            return jjimRes;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
