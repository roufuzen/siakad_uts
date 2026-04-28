package akademik;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {
    private String currentUser;
    private String currentRole;

    public MainFrame(String username, String role) {
        this.currentUser = username;
        this.currentRole = role;
        initComponents();
        setTitle("Sistem Akademik - Departemen Teknik Informatika UTB");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(800, 600);
    }

    private void initComponents() {
        // ========== MENU BAR ==========
        JMenuBar menuBar = new JMenuBar();

        // Menu File Master
        JMenu menuFileMaster = new JMenu("File Master");
        JMenuItem miUser      = new JMenuItem("User");
        JMenuItem miMahasiswa = new JMenuItem("Mahasiswa");
        JMenuItem miMataKuliah= new JMenuItem("Mata Kuliah");
        JMenuItem miDosen     = new JMenuItem("Dosen");
        menuFileMaster.add(miUser);
        menuFileMaster.add(miMahasiswa);
        menuFileMaster.add(miMataKuliah);
        menuFileMaster.add(miDosen);

        // Menu Transaction
        JMenu menuTransaction = new JMenu("Transaction");
        JMenuItem miKRS       = new JMenuItem("KRS");
        JMenuItem miNilai     = new JMenuItem("Nilai");
        JMenuItem miFormUTS   = new JMenuItem("Form Input Nilai");
        menuTransaction.add(miKRS);
        menuTransaction.add(miNilai);
        menuTransaction.addSeparator();
        menuTransaction.add(miFormUTS);

        // Menu Settings
        JMenu menuSettings    = new JMenu("Settings");
        JMenuItem miAddUser   = new JMenuItem("Add User");
        JMenuItem miChangePwd = new JMenuItem("Change Password");
        JMenuItem miLogout    = new JMenuItem("Logout");
        menuSettings.add(miAddUser);
        menuSettings.add(miChangePwd);
        menuSettings.addSeparator();
        menuSettings.add(miLogout);

        menuBar.add(menuFileMaster);
        menuBar.add(menuTransaction);
        menuBar.add(menuSettings);
        setJMenuBar(menuBar);

        // ========== CONTENT PANEL ==========
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(240, 245, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 102, 153));
        headerPanel.setPreferredSize(new Dimension(800, 90));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("SISTEM INFORMASI AKADEMIK");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Departemen Teknik Informatika - Universitas Teknologi Bandung");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSub.setForeground(new Color(200, 230, 255));

        JLabel lblUser = new JLabel("Login sebagai: " + currentUser + " (" + currentRole + ")", JLabel.RIGHT);
        lblUser.setFont(new Font("Arial", Font.ITALIC, 11));
        lblUser.setForeground(Color.WHITE);

        JPanel leftHeader = new JPanel(new GridLayout(2, 1));
        leftHeader.setBackground(new Color(0, 102, 153));
        leftHeader.add(lblTitle);
        leftHeader.add(lblSub);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(lblUser, BorderLayout.EAST);

        // Center: welcome card
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(240, 245, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        String[][] menuItems = {
            {"👤 Mahasiswa", "Data mahasiswa aktif"},
            {"📚 Mata Kuliah", "Kelola mata kuliah"},
            {"👨‍🏫 Dosen", "Data dosen pengajar"},
            {"📋 KRS", "Kartu Rencana Studi"},
            {"📊 Nilai", "Input & lihat nilai"},
            {"📝 Form Penilaian", "Form hitung nilai"}
        };

        int col = 0, row = 0;
        for (String[] item : menuItems) {
    JButton btn = createCardButton(item[0], item[1]);

    String nama = item[0];

    btn.addActionListener(e -> {
        if (nama.contains("Mahasiswa")) {
            new FormMahasiswa().setVisible(true);
        } 
        else if (nama.contains("Mata Kuliah")) {
            new FormMataKuliah().setVisible(true);
        } 
        else if (nama.contains("Dosen")) {
            new FormDosen().setVisible(true);
        } 
        else if (nama.contains("KRS")) {
            new FormKRS().setVisible(true);
        } 
        else if (nama.contains("Nilai")) {
            new FormNilai().setVisible(true);
        } 
        else if (nama.contains("Penilaian")) {
            new FormInputNilai().setVisible(true);
        }
    });

    gbc.gridx = col;
    gbc.gridy = row;
    centerPanel.add(btn, gbc);

    col++;
    if (col == 3) {
        col = 0;
        row++;
    }
}

        // Status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(new Color(220, 230, 240));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.add(new JLabel("  Sistem Akademik UTB  |  User: " + currentUser));

        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(centerPanel), BorderLayout.CENTER);
        contentPanel.add(statusBar, BorderLayout.SOUTH);
        add(contentPanel);

        // ========== ACTION LISTENERS ==========
        miMahasiswa.addActionListener(e -> new FormMahasiswa().setVisible(true));
        miMataKuliah.addActionListener(e -> new FormMataKuliah().setVisible(true));
        miDosen.addActionListener(e -> new FormDosen().setVisible(true));
        miUser.addActionListener(e -> new FormUser().setVisible(true));
        miKRS.addActionListener(e -> new FormKRS().setVisible(true));
        miNilai.addActionListener(e -> new FormNilai().setVisible(true));
        miFormUTS.addActionListener(e -> new FormInputNilai().setVisible(true));
        miAddUser.addActionListener(e -> new FormUser().setVisible(true));
        miChangePwd.addActionListener(e -> new FormChangePassword(currentUser).setVisible(true));
        miLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin logout?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                this.dispose();
            }
        });
    }

    private JButton createCardButton(String title, String subtitle) {
        JButton btn = new JButton("<html><center><b>" + title + "</b><br><small>" + subtitle + "</small></center></html>");
        btn.setPreferredSize(new Dimension(180, 80));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(0, 102, 153)); btn.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(Color.WHITE); btn.setForeground(Color.BLACK); }
        });
        return btn;
    }
}
