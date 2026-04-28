package akademik;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

// ============================================================
// FORM MATA KULIAH
// ============================================================
class FormMataKuliah extends JFrame {
    private JTextField txtKode, txtNama;
    private JSpinner spnSKS, spnSemester;
    private JButton btnSimpan, btnHapus, btnBersih, btnTutup;
    private JTable tabel;
    private DefaultTableModel model;

    public FormMataKuliah() {
        initComponents();
        setTitle("Form Mata Kuliah");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(600, 480);
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel pForm = new JPanel(new GridBagLayout());
        pForm.setBorder(BorderFactory.createTitledBorder("Data Mata Kuliah"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,10,5,10); g.fill = GridBagConstraints.HORIZONTAL;

        addField(pForm, g, "Kode MK:", 0); txtKode = new JTextField(12); addComp(pForm, g, txtKode, 1, 0);
        addField(pForm, g, "Nama MK:", 1); txtNama = new JTextField(12); addComp(pForm, g, txtNama, 1, 1);
        addField(pForm, g, "SKS:", 2);
        spnSKS = new JSpinner(new SpinnerNumberModel(2, 1, 6, 1)); addComp(pForm, g, spnSKS, 1, 2);
        addField(pForm, g, "Semester:", 3);
        spnSemester = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1)); addComp(pForm, g, spnSemester, 1, 3);

        JPanel pBtn = new JPanel(new FlowLayout());
        btnSimpan = makeBtn("Simpan", new Color(0,153,76));
        btnHapus  = makeBtn("Hapus", new Color(204,0,0));
        btnBersih = makeBtn("Bersih", Color.GRAY);
        btnTutup  = makeBtn("Tutup", Color.DARK_GRAY);
        pBtn.add(btnSimpan); pBtn.add(btnHapus); pBtn.add(btnBersih); pBtn.add(btnTutup);

        model = new DefaultTableModel(new String[]{"Kode","Nama MK","SKS","Semester"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel = new JTable(model);
        styleHeader(tabel);

        JPanel top = new JPanel(new BorderLayout());
        top.add(pForm, BorderLayout.CENTER); top.add(pBtn, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        btnSimpan.addActionListener(e -> simpan());
        btnHapus.addActionListener(e -> hapus());
        btnBersih.addActionListener(e -> bersih());
        btnTutup.addActionListener(e -> dispose());
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabel.getSelectedRow() >= 0) {
                int r = tabel.getSelectedRow();
                txtKode.setText(model.getValueAt(r,0).toString());
                txtNama.setText(model.getValueAt(r,1).toString());
                try { spnSKS.setValue(Integer.parseInt(model.getValueAt(r,2).toString())); } catch(Exception ignored){}
                try { spnSemester.setValue(Integer.parseInt(model.getValueAt(r,3).toString())); } catch(Exception ignored){}
            }
        });
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery("SELECT * FROM mata_kuliah ORDER BY kode_mk");
            while (rs.next()) model.addRow(new Object[]{rs.getString("kode_mk"),rs.getString("nama_mk"),rs.getInt("sks"),rs.getInt("semester")});
            rs.close();
        } catch (SQLException ex) { err(ex); }
    }

    private void simpan() {
        String kode = txtKode.getText().trim(), nama = txtNama.getText().trim();
        if (kode.isEmpty() || nama.isEmpty()) { JOptionPane.showMessageDialog(this,"Kode dan Nama wajib diisi!","Peringatan",JOptionPane.WARNING_MESSAGE); return; }
        try {
            Connection c = DBConnection.getConnection();
            PreparedStatement cek = c.prepareStatement("SELECT kode_mk FROM mata_kuliah WHERE kode_mk=?");
            cek.setString(1,kode); ResultSet rs = cek.executeQuery(); boolean ada = rs.next(); rs.close(); cek.close();
            String sql = ada ? "UPDATE mata_kuliah SET nama_mk=?,sks=?,semester=? WHERE kode_mk=?" : "INSERT INTO mata_kuliah VALUES(?,?,?,?)";
            PreparedStatement ps = c.prepareStatement(sql);
            if (ada) { ps.setString(1,nama); ps.setInt(2,(int)spnSKS.getValue()); ps.setInt(3,(int)spnSemester.getValue()); ps.setString(4,kode); }
            else      { ps.setString(1,kode); ps.setString(2,nama); ps.setInt(3,(int)spnSKS.getValue()); ps.setInt(4,(int)spnSemester.getValue()); }
            ps.executeUpdate(); ps.close();
            JOptionPane.showMessageDialog(this,"Data disimpan!"); loadData(); bersih();
        } catch (SQLException ex) { err(ex); }
    }

    private void hapus() {
        if (txtKode.getText().trim().isEmpty()) return;
        if (JOptionPane.showConfirmDialog(this,"Hapus data ini?","Konfirmasi",JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM mata_kuliah WHERE kode_mk=?");
            ps.setString(1,txtKode.getText().trim()); ps.executeUpdate(); ps.close();
            loadData(); bersih();
        } catch (SQLException ex) { err(ex); }
    }

    private void bersih() { txtKode.setText(""); txtNama.setText(""); spnSKS.setValue(2); spnSemester.setValue(1); tabel.clearSelection(); }
    private void addField(JPanel p, GridBagConstraints g, String lbl, int row) { g.gridx=0; g.gridy=row; p.add(new JLabel(lbl),g); }
    private void addComp(JPanel p, GridBagConstraints g, JComponent c, int col, int row) { g.gridx=col; g.gridy=row; p.add(c,g); }
    private JButton makeBtn(String t, Color bg) { JButton b = new JButton(t); b.setBackground(bg); b.setForeground(Color.WHITE); b.setPreferredSize(new Dimension(90,30)); return b; }
    private void styleHeader(JTable t) { t.getTableHeader().setBackground(new Color(0,102,153)); t.getTableHeader().setForeground(Color.WHITE); t.getTableHeader().setFont(new Font("Arial",Font.BOLD,12)); t.setRowHeight(22); }
    private void err(Exception ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
}

// ============================================================
// FORM DOSEN
// ============================================================
class FormDosen extends JFrame {
    private JTextField txtNIP, txtNama, txtBidang;
    private JButton btnSimpan, btnHapus, btnBersih, btnTutup;
    private JTable tabel;
    private DefaultTableModel model;

    public FormDosen() {
        initComponents();
        setTitle("Form Dosen");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(580, 450);
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel pForm = new JPanel(new GridBagLayout());
        pForm.setBorder(BorderFactory.createTitledBorder("Data Dosen"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,10,6,10); g.fill = GridBagConstraints.HORIZONTAL;

        String[][] fields = {{"NIP:","0"},{"Nama:","1"},{"Bidang:","2"}};
        JTextField[] tfs = {txtNIP=new JTextField(15), txtNama=new JTextField(15), txtBidang=new JTextField(15)};
        for (int i = 0; i < fields.length; i++) {
            g.gridx=0; g.gridy=Integer.parseInt(fields[i][1]); pForm.add(new JLabel(fields[i][0]),g);
            g.gridx=1; pForm.add(tfs[i],g);
        }

        JPanel pBtn = new JPanel(new FlowLayout());
        btnSimpan = makeBtn("Simpan",new Color(0,153,76));
        btnHapus  = makeBtn("Hapus",new Color(204,0,0));
        btnBersih = makeBtn("Bersih",Color.GRAY);
        btnTutup  = makeBtn("Tutup",Color.DARK_GRAY);
        pBtn.add(btnSimpan); pBtn.add(btnHapus); pBtn.add(btnBersih); pBtn.add(btnTutup);

        model = new DefaultTableModel(new String[]{"NIP","Nama","Bidang"},0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel = new JTable(model);
        tabel.getTableHeader().setBackground(new Color(0,102,153));
        tabel.getTableHeader().setForeground(Color.WHITE);
        tabel.setRowHeight(22);

        JPanel top = new JPanel(new BorderLayout());
        top.add(pForm,BorderLayout.CENTER); top.add(pBtn,BorderLayout.SOUTH);
        add(top,BorderLayout.NORTH); add(new JScrollPane(tabel),BorderLayout.CENTER);

        btnSimpan.addActionListener(e -> simpan());
        btnHapus.addActionListener(e -> hapus());
        btnBersih.addActionListener(e -> bersih());
        btnTutup.addActionListener(e -> dispose());
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabel.getSelectedRow() >= 0) {
                int r = tabel.getSelectedRow();
                txtNIP.setText(model.getValueAt(r,0).toString());
                txtNama.setText(model.getValueAt(r,1).toString());
                txtBidang.setText(model.getValueAt(r,2).toString());
            }
        });
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery("SELECT * FROM dosen ORDER BY nip");
            while (rs.next()) model.addRow(new Object[]{rs.getString("nip"),rs.getString("nama"),rs.getString("bidang")});
            rs.close();
        } catch (SQLException ex) { err(ex); }
    }

    private void simpan() {
        String nip = txtNIP.getText().trim(), nama = txtNama.getText().trim(), bidang = txtBidang.getText().trim();
        if (nip.isEmpty() || nama.isEmpty()) { JOptionPane.showMessageDialog(this,"NIP dan Nama wajib!","Peringatan",JOptionPane.WARNING_MESSAGE); return; }
        try {
            Connection c = DBConnection.getConnection();
            PreparedStatement cek = c.prepareStatement("SELECT nip FROM dosen WHERE nip=?");
            cek.setString(1,nip); boolean ada = cek.executeQuery().next(); cek.close();
            String sql = ada ? "UPDATE dosen SET nama=?,bidang=? WHERE nip=?" : "INSERT INTO dosen VALUES(?,?,?)";
            PreparedStatement ps = c.prepareStatement(sql);
            if (ada) { ps.setString(1,nama); ps.setString(2,bidang); ps.setString(3,nip); }
            else      { ps.setString(1,nip);  ps.setString(2,nama);  ps.setString(3,bidang); }
            ps.executeUpdate(); ps.close();
            JOptionPane.showMessageDialog(this,"Data disimpan!"); loadData(); bersih();
        } catch (SQLException ex) { err(ex); }
    }

    private void hapus() {
        if (txtNIP.getText().trim().isEmpty()) return;
        if (JOptionPane.showConfirmDialog(this,"Hapus data ini?","Konfirmasi",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM dosen WHERE nip=?");
            ps.setString(1,txtNIP.getText().trim()); ps.executeUpdate(); ps.close();
            loadData(); bersih();
        } catch (SQLException ex) { err(ex); }
    }

    private void bersih() { txtNIP.setText(""); txtNama.setText(""); txtBidang.setText(""); tabel.clearSelection(); }
    private JButton makeBtn(String t, Color bg) { JButton b = new JButton(t); b.setBackground(bg); b.setForeground(Color.WHITE); b.setPreferredSize(new Dimension(90,30)); return b; }
    private void err(Exception ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
}

// ============================================================
// FORM USER (Add User)
// ============================================================
class FormUser extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword, txtKonfirmasi;
    private JComboBox<String> cboRole;
    private JButton btnSimpan, btnHapus, btnBersih, btnTutup;
    private JTable tabel;
    private DefaultTableModel model;

    public FormUser() {
        initComponents();
        setTitle("Manajemen User");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(550, 430);
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel pForm = new JPanel(new GridBagLayout());
        pForm.setBorder(BorderFactory.createTitledBorder("Data User"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,10,6,10); g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx=0; g.gridy=0; pForm.add(new JLabel("Username:"),g);
        g.gridx=1; txtUsername=new JTextField(15); pForm.add(txtUsername,g);
        g.gridx=0; g.gridy=1; pForm.add(new JLabel("Password:"),g);
        g.gridx=1; txtPassword=new JPasswordField(15); pForm.add(txtPassword,g);
        g.gridx=0; g.gridy=2; pForm.add(new JLabel("Konfirmasi Password:"),g);
        g.gridx=1; txtKonfirmasi=new JPasswordField(15); pForm.add(txtKonfirmasi,g);
        g.gridx=0; g.gridy=3; pForm.add(new JLabel("Role:"),g);
        g.gridx=1; cboRole=new JComboBox<>(new String[]{"user","admin"}); pForm.add(cboRole,g);

        JPanel pBtn = new JPanel(new FlowLayout());
        btnSimpan = makeBtn("Simpan",new Color(0,153,76));
        btnHapus  = makeBtn("Hapus",new Color(204,0,0));
        btnBersih = makeBtn("Bersih",Color.GRAY);
        btnTutup  = makeBtn("Tutup",Color.DARK_GRAY);
        pBtn.add(btnSimpan); pBtn.add(btnHapus); pBtn.add(btnBersih); pBtn.add(btnTutup);

        model = new DefaultTableModel(new String[]{"ID","Username","Role"},0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel = new JTable(model);
        tabel.getTableHeader().setBackground(new Color(0,102,153));
        tabel.getTableHeader().setForeground(Color.WHITE);
        tabel.setRowHeight(22);

        JPanel top = new JPanel(new BorderLayout());
        top.add(pForm,BorderLayout.CENTER); top.add(pBtn,BorderLayout.SOUTH);
        add(top,BorderLayout.NORTH); add(new JScrollPane(tabel),BorderLayout.CENTER);

        btnSimpan.addActionListener(e -> simpan());
        btnHapus.addActionListener(e -> hapus());
        btnBersih.addActionListener(e -> bersih());
        btnTutup.addActionListener(e -> dispose());
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabel.getSelectedRow() >= 0) {
                int r = tabel.getSelectedRow();
                txtUsername.setText(model.getValueAt(r,1).toString());
                cboRole.setSelectedItem(model.getValueAt(r,2).toString());
            }
        });
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery("SELECT id,username,role FROM users ORDER BY id");
            while (rs.next()) model.addRow(new Object[]{rs.getInt("id"),rs.getString("username"),rs.getString("role")});
            rs.close();
        } catch (SQLException ex) { err(ex); }
    }

    private void simpan() {
        String uname = txtUsername.getText().trim();
        String pwd   = new String(txtPassword.getPassword()).trim();
        String kpwd  = new String(txtKonfirmasi.getPassword()).trim();
        String role  = cboRole.getSelectedItem().toString();

        if (uname.isEmpty() || pwd.isEmpty()) { JOptionPane.showMessageDialog(this,"Username dan Password wajib!","Peringatan",JOptionPane.WARNING_MESSAGE); return; }
        if (!pwd.equals(kpwd)) { JOptionPane.showMessageDialog(this,"Password tidak cocok!","Peringatan",JOptionPane.WARNING_MESSAGE); return; }

        try {
            Connection c = DBConnection.getConnection();
            PreparedStatement cek = c.prepareStatement("SELECT id FROM users WHERE username=?");
            cek.setString(1,uname); boolean ada = cek.executeQuery().next(); cek.close();
            if (ada) { JOptionPane.showMessageDialog(this,"Username sudah ada!","Peringatan",JOptionPane.WARNING_MESSAGE); return; }
            PreparedStatement ps = c.prepareStatement("INSERT INTO users (username,password,role) VALUES(?,?,?)");
            ps.setString(1,uname); ps.setString(2,pwd); ps.setString(3,role);
            ps.executeUpdate(); ps.close();
            JOptionPane.showMessageDialog(this,"User berhasil ditambahkan!"); loadData(); bersih();
        } catch (SQLException ex) { err(ex); }
    }

    private void hapus() {
        int row = tabel.getSelectedRow();
        if (row < 0) return;
        String uname = model.getValueAt(row,1).toString();
        if ("admin".equals(uname)) { JOptionPane.showMessageDialog(this,"User admin tidak dapat dihapus!","Peringatan",JOptionPane.WARNING_MESSAGE); return; }
        if (JOptionPane.showConfirmDialog(this,"Hapus user '"+uname+"'?","Konfirmasi",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM users WHERE id=?");
            ps.setInt(1,(int)model.getValueAt(row,0)); ps.executeUpdate(); ps.close();
            loadData(); bersih();
        } catch (SQLException ex) { err(ex); }
    }

    private void bersih() { txtUsername.setText(""); txtPassword.setText(""); txtKonfirmasi.setText(""); cboRole.setSelectedIndex(0); tabel.clearSelection(); }
    private JButton makeBtn(String t, Color bg) { JButton b = new JButton(t); b.setBackground(bg); b.setForeground(Color.WHITE); b.setPreferredSize(new Dimension(90,30)); return b; }
    private void err(Exception ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
}

// ============================================================
// FORM KRS
// ============================================================
class FormKRS extends JFrame {
    private JComboBox<String> cboNIM, cboKodeMK;
    private JTextField txtSemester, txtTahunAkademik;
    private JButton btnSimpan, btnHapus, btnBersih, btnTutup;
    private JTable tabel;
    private DefaultTableModel model;

    public FormKRS() {
        initComponents();
        setTitle("Form KRS (Kartu Rencana Studi)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(650, 480);
        loadCombo();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel pForm = new JPanel(new GridBagLayout());
        pForm.setBorder(BorderFactory.createTitledBorder("Data KRS"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,10,6,10); g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx=0; g.gridy=0; pForm.add(new JLabel("NIM Mahasiswa:"),g);
        g.gridx=1; cboNIM=new JComboBox<>(); cboNIM.setPreferredSize(new Dimension(200,25)); pForm.add(cboNIM,g);
        g.gridx=0; g.gridy=1; pForm.add(new JLabel("Mata Kuliah:"),g);
        g.gridx=1; cboKodeMK=new JComboBox<>(); cboKodeMK.setPreferredSize(new Dimension(200,25)); pForm.add(cboKodeMK,g);
        g.gridx=0; g.gridy=2; pForm.add(new JLabel("Semester:"),g);
        g.gridx=1; txtSemester=new JTextField("Ganjil",12); pForm.add(txtSemester,g);
        g.gridx=0; g.gridy=3; pForm.add(new JLabel("Tahun Akademik:"),g);
        g.gridx=1; txtTahunAkademik=new JTextField("2024/2025",12); pForm.add(txtTahunAkademik,g);

        JPanel pBtn = new JPanel(new FlowLayout());
        btnSimpan = makeBtn("Simpan",new Color(0,153,76));
        btnHapus  = makeBtn("Hapus",new Color(204,0,0));
        btnBersih = makeBtn("Bersih",Color.GRAY);
        btnTutup  = makeBtn("Tutup",Color.DARK_GRAY);
        pBtn.add(btnSimpan); pBtn.add(btnHapus); pBtn.add(btnBersih); pBtn.add(btnTutup);

        model = new DefaultTableModel(new String[]{"ID","NIM","Nama MK","Semester","Tahun"},0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel = new JTable(model);
        tabel.getTableHeader().setBackground(new Color(0,102,153));
        tabel.getTableHeader().setForeground(Color.WHITE);
        tabel.setRowHeight(22);

        JPanel top = new JPanel(new BorderLayout());
        top.add(pForm,BorderLayout.CENTER); top.add(pBtn,BorderLayout.SOUTH);
        add(top,BorderLayout.NORTH); add(new JScrollPane(tabel),BorderLayout.CENTER);

        btnSimpan.addActionListener(e -> simpan());
        btnHapus.addActionListener(e -> hapus());
        btnBersih.addActionListener(e -> bersih());
        btnTutup.addActionListener(e -> dispose());
    }

    private void loadCombo() {
        try {
            cboNIM.removeAllItems(); cboKodeMK.removeAllItems();
            ResultSet r1 = DBConnection.getConnection().createStatement().executeQuery("SELECT nim,nama FROM mahasiswa ORDER BY nim");
            while (r1.next()) cboNIM.addItem(r1.getString("nim")+" - "+r1.getString("nama"));
            r1.close();
            ResultSet r2 = DBConnection.getConnection().createStatement().executeQuery("SELECT kode_mk,nama_mk FROM mata_kuliah ORDER BY kode_mk");
            while (r2.next()) cboKodeMK.addItem(r2.getString("kode_mk")+" - "+r2.getString("nama_mk"));
            r2.close();
        } catch (SQLException ex) { err(ex); }
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            String sql = "SELECT k.id, k.nim, m.nama_mk, k.semester, k.tahun_akademik FROM krs k JOIN mata_kuliah m ON k.kode_mk=m.kode_mk ORDER BY k.id";
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
            while (rs.next()) model.addRow(new Object[]{rs.getInt("id"),rs.getString("nim"),rs.getString("nama_mk"),rs.getString("semester"),rs.getString("tahun_akademik")});
            rs.close();
        } catch (SQLException ex) { err(ex); }
    }

    private void simpan() {
        if (cboNIM.getItemCount() == 0 || cboKodeMK.getItemCount() == 0) { JOptionPane.showMessageDialog(this,"Data mahasiswa/MK kosong!","Peringatan",JOptionPane.WARNING_MESSAGE); return; }
        String nim = cboNIM.getSelectedItem().toString().split(" - ")[0];
        String kode = cboKodeMK.getSelectedItem().toString().split(" - ")[0];
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement("INSERT INTO krs (nim,kode_mk,semester,tahun_akademik) VALUES(?,?,?,?)");
            ps.setString(1,nim); ps.setString(2,kode); ps.setString(3,txtSemester.getText().trim()); ps.setString(4,txtTahunAkademik.getText().trim());
            ps.executeUpdate(); ps.close();
            JOptionPane.showMessageDialog(this,"KRS berhasil disimpan!"); loadData();
        } catch (SQLException ex) { err(ex); }
    }

    private void hapus() {
        int row = tabel.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Pilih data yang akan dihapus!","Peringatan",JOptionPane.WARNING_MESSAGE); return; }
        if (JOptionPane.showConfirmDialog(this,"Hapus KRS ini?","Konfirmasi",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM krs WHERE id=?");
            ps.setInt(1,(int)model.getValueAt(row,0)); ps.executeUpdate(); ps.close();
            loadData();
        } catch (SQLException ex) { err(ex); }
    }

    private void bersih() { tabel.clearSelection(); }
    private JButton makeBtn(String t, Color bg) { JButton b = new JButton(t); b.setBackground(bg); b.setForeground(Color.WHITE); b.setPreferredSize(new Dimension(90,30)); return b; }
    private void err(Exception ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
}

// ============================================================
// FORM NILAI (Huruf: A, B, C, D, E)
// ============================================================
class FormNilai extends JFrame {
    private JComboBox<String> cboNIM, cboKodeMK, cboNilai;
    private JTextField txtSemester;
    private JButton btnSimpan, btnHapus, btnBersih, btnTutup;
    private JTable tabel;
    private DefaultTableModel model;

    public FormNilai() {
        initComponents();
        setTitle("Form Nilai");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(650, 450);
        loadCombo();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel pForm = new JPanel(new GridBagLayout());
        pForm.setBorder(BorderFactory.createTitledBorder("Input Nilai (Huruf: A, B, C, D, E)"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,10,6,10); g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx=0; g.gridy=0; pForm.add(new JLabel("NIM Mahasiswa:"),g);
        g.gridx=1; cboNIM=new JComboBox<>(); cboNIM.setPreferredSize(new Dimension(200,25)); pForm.add(cboNIM,g);
        g.gridx=0; g.gridy=1; pForm.add(new JLabel("Mata Kuliah:"),g);
        g.gridx=1; cboKodeMK=new JComboBox<>(); cboKodeMK.setPreferredSize(new Dimension(200,25)); pForm.add(cboKodeMK,g);
        g.gridx=0; g.gridy=2; pForm.add(new JLabel("Semester:"),g);
        g.gridx=1; txtSemester=new JTextField("Ganjil",12); pForm.add(txtSemester,g);
        g.gridx=0; g.gridy=3; pForm.add(new JLabel("Nilai Huruf:"),g);
        g.gridx=1; cboNilai=new JComboBox<>(new String[]{"A","B","C","D","E"});
        cboNilai.setPreferredSize(new Dimension(80,25)); pForm.add(cboNilai,g);

        JPanel pInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pInfo.add(new JLabel("  Keterangan: A≥80 | B≥70 | C≥60 | D≥50 | E<50"));
        ((JLabel)pInfo.getComponent(0)).setFont(new Font("Arial",Font.ITALIC,11));
        ((JLabel)pInfo.getComponent(0)).setForeground(Color.GRAY);

        JPanel pBtn = new JPanel(new FlowLayout());
        btnSimpan = makeBtn("Simpan",new Color(0,153,76));
        btnHapus  = makeBtn("Hapus",new Color(204,0,0));
        btnBersih = makeBtn("Bersih",Color.GRAY);
        btnTutup  = makeBtn("Tutup",Color.DARK_GRAY);
        pBtn.add(btnSimpan); pBtn.add(btnHapus); pBtn.add(btnBersih); pBtn.add(btnTutup);

        model = new DefaultTableModel(new String[]{"ID","NIM","Nama Mahasiswa","Mata Kuliah","Semester","Nilai"},0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel = new JTable(model);
        tabel.getTableHeader().setBackground(new Color(0,102,153));
        tabel.getTableHeader().setForeground(Color.WHITE);
        tabel.setRowHeight(22);

        JPanel top = new JPanel(new BorderLayout());
        JPanel formArea = new JPanel(new BorderLayout());
        formArea.add(pForm,BorderLayout.CENTER); formArea.add(pInfo,BorderLayout.SOUTH);
        top.add(formArea,BorderLayout.CENTER); top.add(pBtn,BorderLayout.SOUTH);
        add(top,BorderLayout.NORTH); add(new JScrollPane(tabel),BorderLayout.CENTER);

        btnSimpan.addActionListener(e -> simpan());
        btnHapus.addActionListener(e -> hapus());
        btnBersih.addActionListener(e -> tabel.clearSelection());
        btnTutup.addActionListener(e -> dispose());
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabel.getSelectedRow() >= 0) {
                int r = tabel.getSelectedRow();
                cboNilai.setSelectedItem(model.getValueAt(r,5).toString());
            }
        });
    }

    private void loadCombo() {
        try {
            cboNIM.removeAllItems(); cboKodeMK.removeAllItems();
            ResultSet r1 = DBConnection.getConnection().createStatement().executeQuery("SELECT nim,nama FROM mahasiswa");
            while (r1.next()) cboNIM.addItem(r1.getString("nim")+" - "+r1.getString("nama"));
            r1.close();
            ResultSet r2 = DBConnection.getConnection().createStatement().executeQuery("SELECT kode_mk,nama_mk FROM mata_kuliah");
            while (r2.next()) cboKodeMK.addItem(r2.getString("kode_mk")+" - "+r2.getString("nama_mk"));
            r2.close();
        } catch (SQLException ex) { err(ex); }
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            String sql = "SELECT n.id, n.nim, m.nama AS nama_mhs, mk.nama_mk, n.semester, n.nilai_huruf FROM nilai n JOIN mahasiswa m ON n.nim=m.nim JOIN mata_kuliah mk ON n.kode_mk=mk.kode_mk ORDER BY n.id";
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
            while (rs.next()) model.addRow(new Object[]{rs.getInt("id"),rs.getString("nim"),rs.getString("nama_mhs"),rs.getString("nama_mk"),rs.getString("semester"),rs.getString("nilai_huruf")});
            rs.close();
        } catch (SQLException ex) { err(ex); }
    }

    private void simpan() {
        if (cboNIM.getItemCount()==0 || cboKodeMK.getItemCount()==0) return;
        String nim = cboNIM.getSelectedItem().toString().split(" - ")[0];
        String kode = cboKodeMK.getSelectedItem().toString().split(" - ")[0];
        String nilaiHuruf = cboNilai.getSelectedItem().toString();
        String semester = txtSemester.getText().trim();
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement("INSERT INTO nilai (nim,kode_mk,semester,nilai_huruf) VALUES(?,?,?,?)");
            ps.setString(1,nim); ps.setString(2,kode); ps.setString(3,semester); ps.setString(4,nilaiHuruf);
            ps.executeUpdate(); ps.close();
            JOptionPane.showMessageDialog(this,"Nilai berhasil disimpan!"); loadData();
        } catch (SQLException ex) { err(ex); }
    }

    private void hapus() {
        int row = tabel.getSelectedRow();
        if (row < 0) return;
        if (JOptionPane.showConfirmDialog(this,"Hapus nilai ini?","Konfirmasi",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM nilai WHERE id=?");
            ps.setInt(1,(int)model.getValueAt(row,0)); ps.executeUpdate(); ps.close();
            loadData();
        } catch (SQLException ex) { err(ex); }
    }

    private JButton makeBtn(String t, Color bg) { JButton b = new JButton(t); b.setBackground(bg); b.setForeground(Color.WHITE); b.setPreferredSize(new Dimension(90,30)); return b; }
    private void err(Exception ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
}

// ============================================================
// FORM CHANGE PASSWORD
// ============================================================
class FormChangePassword extends JFrame {
    private String currentUser;
    private JPasswordField txtLama, txtBaru, txtKonfirmasi;
    private JButton btnSimpan, btnBatal;

    public FormChangePassword(String user) {
        this.currentUser = user;
        initComponents();
        setTitle("Ganti Password - " + user);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20,30,20,30));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8,5,8,5); g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx=0; g.gridy=0; panel.add(new JLabel("Password Lama:"),g);
        g.gridx=1; txtLama=new JPasswordField(15); panel.add(txtLama,g);
        g.gridx=0; g.gridy=1; panel.add(new JLabel("Password Baru:"),g);
        g.gridx=1; txtBaru=new JPasswordField(15); panel.add(txtBaru,g);
        g.gridx=0; g.gridy=2; panel.add(new JLabel("Konfirmasi Baru:"),g);
        g.gridx=1; txtKonfirmasi=new JPasswordField(15); panel.add(txtKonfirmasi,g);

        JPanel pBtn = new JPanel(new FlowLayout());
        btnSimpan = new JButton("Simpan"); btnSimpan.setBackground(new Color(0,102,153)); btnSimpan.setForeground(Color.WHITE); btnSimpan.setPreferredSize(new Dimension(100,32));
        btnBatal  = new JButton("Batal");  btnBatal.setPreferredSize(new Dimension(100,32));
        pBtn.add(btnSimpan); pBtn.add(btnBatal);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(pBtn,  BorderLayout.SOUTH);
        pack(); setSize(350, 230);

        btnSimpan.addActionListener(e -> simpan());
        btnBatal.addActionListener(e -> dispose());
    }

    private void simpan() {
        String lama = new String(txtLama.getPassword());
        String baru = new String(txtBaru.getPassword());
        String konfirm = new String(txtKonfirmasi.getPassword());
        if (lama.isEmpty() || baru.isEmpty()) { JOptionPane.showMessageDialog(this,"Semua field wajib diisi!","Peringatan",JOptionPane.WARNING_MESSAGE); return; }
        if (!baru.equals(konfirm)) { JOptionPane.showMessageDialog(this,"Password baru tidak cocok!","Peringatan",JOptionPane.WARNING_MESSAGE); return; }
        if (baru.length() < 6) { JOptionPane.showMessageDialog(this,"Password minimal 6 karakter!","Peringatan",JOptionPane.WARNING_MESSAGE); return; }
        try {
            Connection c = DBConnection.getConnection();
            PreparedStatement cek = c.prepareStatement("SELECT id FROM users WHERE username=? AND password=?");
            cek.setString(1,currentUser); cek.setString(2,lama);
            if (!cek.executeQuery().next()) { JOptionPane.showMessageDialog(this,"Password lama salah!","Error",JOptionPane.ERROR_MESSAGE); return; }
            cek.close();
            PreparedStatement ps = c.prepareStatement("UPDATE users SET password=? WHERE username=?");
            ps.setString(1,baru); ps.setString(2,currentUser); ps.executeUpdate(); ps.close();
            JOptionPane.showMessageDialog(this,"Password berhasil diubah!","Sukses",JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
    }
}
