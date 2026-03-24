package com.mpole.hdt.digitaltwin.persistence.menu;

import com.mpole.hdt.digitaltwin.api.dto.menu.MenuPutSortOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MenuBulkRepository {
    private final JdbcTemplate jdbcTemplate;


    @Transactional
    public void updateMenuSortOrder(List<MenuPutSortOrderRequest> reqs) {
        jdbcTemplate.batchUpdate(
                "UPDATE tbl_menu SET sort_order = ? WHERE menu_id = ?",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1,reqs.get(i).sortOrder());
                        ps.setLong(2,reqs.get(i).menuId());
                    }

                    @Override
                    public int getBatchSize() {
                        return reqs.size();
                    }
                }
        );
    }
}
