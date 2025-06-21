package dao;

import entity.Feature;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import util.DBConnection;

public class FeatureDAO {

    public List<Feature> getFeaturesByUserId(int userId) throws SQLException {
        List<Feature> features = new ArrayList<>();
        String sql = "SELECT DISTINCT f.feature_id, f.feature_name " +
                     "FROM features f " +
                     "JOIN role_features rf ON f.feature_id = rf.feature_id " +
                     "JOIN user_roles ur ON rf.role_id = ur.role_id " +
                     "WHERE ur.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Feature feature = new Feature();
                feature.setFeatureId(rs.getInt("feature_id"));
                feature.setFeatureName(rs.getString("feature_name"));
                features.add(feature);
            }
        }
        return features;
    }
}
