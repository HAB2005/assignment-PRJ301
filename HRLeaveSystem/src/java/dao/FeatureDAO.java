package dao;

import entity.Feature;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import util.DBConnection;

public class FeatureDAO {

    public List<Feature> getFeaturesByUserId(int userId) throws SQLException {
        List<Feature> features = new ArrayList<>();
        String sql = "SELECT DISTINCT f.feature_id, f.feature_name "
                + "FROM features f "
                + "JOIN user_features uf ON f.feature_id = uf.feature_id "
                + "WHERE uf.user_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    public List<Feature> getAllFeatures() {
        List<Feature> list = new ArrayList<>();
        String sql = "SELECT feature_id, feature_name FROM features";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Feature f = new Feature();
                f.setFeatureId(rs.getInt("feature_id"));
                f.setFeatureName(rs.getString("feature_name"));
                list.add(f);
            }

        } catch (Exception e) {
        }
        return list;
    }
}
