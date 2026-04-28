package akademik;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnExit;

    public LoginFrame() {
        initComponents();
        setTitle("Login - Sistem Akademik UTB");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 102, 153));
        headerPanel.setPreferredSize(new Dimension(400, 80));
        JLabel lblTitle = new JLabel("SISTEM AKADEMIK", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        JLabel lblSubtitle = new JLabel("Universitas Teknologi Bandung", JLabel.CENTER);
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSubtitle.setForeground(Color.WHITE);
        headerPanel.setLayout(new GridLayout(2, 1));
        headerPanel.add(lblTitle);
        headerPanel.add(lblSubtitle);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        formPanel.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        formPanel.add(txtPassword, gbc);

        // Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(0, 102, 153));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(100, 35));
        btnExit = new JButton("Keluar");
        btnExit.setPreferredSize(new Dimension(100, 35));
        btnPanel.add(btnLogin);
        btnPanel.add(btnExit);

        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setSize(400, 300);

        // Events
        btnLogin.addActionListener(e -> doLogin());
        btnExit.addActionListener(e -> System.exit(0));
        txtPassword.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login berhasil! Selamat datang, " + username, "Sukses", JOptionPane.INFORMATION_MESSAGE);
                new MainFrame(username, rs.getString("role")).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
            }
            rs.close(); ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error koneksi database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
