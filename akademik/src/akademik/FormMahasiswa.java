package akademik;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FormMahasiswa extends JFrame {
    private JTextField txtNIM, txtNama, txtJurusan;
    private JSpinner spnAngkatan;
    private JButton btnSimpan, btnHapus, btnBersih, btnTutup;
    private JTable tabel;
    private DefaultTableModel modelTabel;

    public FormMahasiswa() {
        initComponents();
        setTitle("Form Mahasiswa");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(650, 500);
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));

        // Panel Form
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Data Mahasiswa"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"NIM:", "Nama:", "Jurusan:", "Angkatan:"};
        gbc.gridx = 0; gbc.gridy = 0; panelForm.add(new JLabel(labels[0]), gbc);
        gbc.gridx = 1; txtNIM = new JTextField(15); panelForm.add(txtNIM, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panelForm.add(new JLabel(labels[1]), gbc);
        gbc.gridx = 1; txtNama = new JTextField(15); panelForm.add(txtNama, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panelForm.add(new JLabel(labels[2]), gbc);
        gbc.gridx = 1; txtJurusan = new JTextField(15); panelForm.add(txtJurusan, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panelForm.add(new JLabel(labels[3]), gbc);
        gbc.gridx = 1;
        spnAngkatan = new JSpinner(new SpinnerNumberModel(2024, 2000, 2030, 1));
        ((JSpinner.DefaultEditor) spnAngkatan.getEditor()).getTextField().setColumns(10);
        panelForm.add(spnAngkatan, gbc);

        // Buttons
        JPanel panelBtn = new JPanel(new FlowLayout());
        btnSimpan = createBtn("Simpan", new Color(0, 153, 76));
        btnHapus  = createBtn("Hapus", new Color(204, 0, 0));
        btnBersih = createBtn("Bersih", new Color(102, 102, 102));
        btnTutup  = createBtn("Tutup", new Color(100, 100, 100));
        panelBtn.add(btnSimpan); panelBtn.add(btnHapus);
        panelBtn.add(btnBersih); panelBtn.add(btnTutup);

        // Table
        String[] kolom = {"NIM", "Nama", "Jurusan", "Angkatan"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel = new JTable(modelTabel);
        tabel.setRowHeight(22);
        styleTable(tabel);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(panelForm, BorderLayout.CENTER);
        topPanel.add(panelBtn, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        // Events
        btnSimpan.addActionListener(e -> simpanData());
        btnHapus.addActionListener(e -> hapusData());
        btnBersih.addActionListener(e -> bersihForm());
        btnTutup.addActionListener(e -> dispose());
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabel.getSelectedRow() >= 0) isiFormDariTabel();
        });
    }

    private void loadData() {
        modelTabel.setRowCount(0);
        try {
            Connection conn = DBConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM mahasiswa ORDER BY nim");
            while (rs.next()) {
                modelTabel.addRow(new Object[]{rs.getString("nim"), rs.getString("nama"),
                    rs.getString("jurusan"), rs.getString("angkatan")});
            }
            rs.close();
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void simpanData() {
        String nim = txtNIM.getText().trim();
        String nama = txtNama.getText().trim();
        String jurusan = txtJurusan.getText().trim();
        int angkatan = (int) spnAngkatan.getValue();

        if (nim.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIM dan Nama wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            // Cek apakah sudah ada
            PreparedStatement cek = conn.prepareStatement("SELECT nim FROM mahasiswa WHERE nim = ?");
            cek.setString(1, nim);
            ResultSet rs = cek.executeQuery();
            String sql = rs.next()
                ? "UPDATE mahasiswa SET nama=?, jurusan=?, angkatan=? WHERE nim=?"
                : "INSERT INTO mahasiswa (nama, jurusan, angkatan, nim) VALUES (?, ?, ?, ?)";
            rs.close(); cek.close();

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nama); ps.setString(2, jurusan);
            ps.setInt(3, angkatan); ps.setString(4, nim);
            ps.executeUpdate(); ps.close();

            JOptionPane.showMessageDialog(this, "Data berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadData(); bersihForm();
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void hapusData() {
        if (txtNIM.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data NIM: " + txtNIM.getText() + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM mahasiswa WHERE nim = ?");
            ps.setString(1, txtNIM.getText().trim());
            ps.executeUpdate(); ps.close();
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadData(); bersihForm();
        } catch (SQLException ex) { showError(ex.getMessage()); }
    }

    private void isiFormDariTabel() {
        int row = tabel.getSelectedRow();
        txtNIM.setText(modelTabel.getValueAt(row, 0).toString());
        txtNama.setText(modelTabel.getValueAt(row, 1).toString());
        txtJurusan.setText(modelTabel.getValueAt(row, 2).toString());
        try { spnAngkatan.setValue(Integer.parseInt(modelTabel.getValueAt(row, 3).toString())); } catch (Exception ignored) {}
    }

    private void bersihForm() {
        txtNIM.setText(""); txtNama.setText(""); txtJurusan.setText("");
        spnAngkatan.setValue(2024); tabel.clearSelection();
    }

    private JButton createBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(90, 30)); return btn;
    }

    private void styleTable(JTable t) {
        t.getTableHeader().setBackground(new Color(0, 102, 153));
        t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        t.setRowHeight(22);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, "Error: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
