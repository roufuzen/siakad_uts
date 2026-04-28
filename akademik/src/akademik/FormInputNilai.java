package akademik;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class FormInputNilai extends JFrame {

    private static final int MAX_DATA = 5;

    // Array penyimpanan data
    private String[] arrNIM        = new String[MAX_DATA];
    private double[] arrUTS        = new double[MAX_DATA];
    private double[] arrTugas      = new double[MAX_DATA];
    private double[] arrUAS        = new double[MAX_DATA];
    private double[] arrAbsensi    = new double[MAX_DATA];
    private double[] arrAkhir      = new double[MAX_DATA];
    private int jumlahData         = 0;

    // Komponen UI
    private JTextField txtNIM, txtUTS, txtTugas, txtUAS, txtAbsensi;
    private JButton btnInput, btnClear, btnClose;
    private JTable tabel;
    private DefaultTableModel modelTabel;
    private JLabel lblStatus;

    public FormInputNilai() {
        initComponents();
        setTitle("FORM INPUT NILAI (Maks " + MAX_DATA + " Data)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));

        // ===== PANEL ATAS (Form Input) =====
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "FORM SOAL UTS",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 12)));
        panelForm.setBackground(new Color(245, 248, 252));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        lblStatus = new JLabel("Masukkan Jumlah Data Maksimal " + MAX_DATA);
        lblStatus.setFont(new Font("Arial", Font.ITALIC, 11));
        lblStatus.setForeground(new Color(0, 102, 153));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelForm.add(lblStatus, gbc);
        gbc.gridwidth = 1;

        // NIM
        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("NIM"), gbc);
        gbc.gridx = 1;
        txtNIM = new JTextField(18);
        panelForm.add(txtNIM, gbc);

        // Nilai UTS
        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Nilai UTS"), gbc);
        gbc.gridx = 1;
        txtUTS = new JTextField(18);
        panelForm.add(txtUTS, gbc);

        // Nilai Tugas
        gbc.gridx = 0; gbc.gridy = 3;
        panelForm.add(new JLabel("Nilai Tugas"), gbc);
        gbc.gridx = 1;
        txtTugas = new JTextField(18);
        panelForm.add(txtTugas, gbc);

        // Nilai UAS
        gbc.gridx = 0; gbc.gridy = 4;
        panelForm.add(new JLabel("Nilai UAS"), gbc);
        gbc.gridx = 1;
        txtUAS = new JTextField(18);
        panelForm.add(txtUAS, gbc);

        // Nilai Absensi
        gbc.gridx = 0; gbc.gridy = 5;
        panelForm.add(new JLabel("Nilai Absensi"), gbc);
        gbc.gridx = 1;
        txtAbsensi = new JTextField(18);
        panelForm.add(txtAbsensi, gbc);

        // Tombol
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        panelBtn.setBackground(new Color(245, 248, 252));

        btnInput = new JButton("Input");
        btnInput.setBackground(new Color(0, 102, 153));
        btnInput.setForeground(Color.WHITE);
        btnInput.setPreferredSize(new Dimension(90, 30));

        btnClear = new JButton("Clear");
        btnClear.setPreferredSize(new Dimension(90, 30));

        btnClose = new JButton("Close");
        btnClose.setBackground(new Color(180, 0, 0));
        btnClose.setForeground(Color.WHITE);
        btnClose.setPreferredSize(new Dimension(90, 30));

        panelBtn.add(btnInput);
        panelBtn.add(btnClear);
        panelBtn.add(btnClose);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelForm.add(panelBtn, gbc);

        // ===== PANEL BAWAH (Tabel) =====
        JPanel panelTabel = new JPanel(new BorderLayout(5, 5));
        panelTabel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "DATA YANG SUDAH DIINPUT",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 11)));

        String[] kolom = {"NIM", "UTS", "Tugas", "UAS", "Absensi", "Akhir", "Grade"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel = new JTable(modelTabel);
        tabel.setRowHeight(22);
        tabel.getTableHeader().setBackground(new Color(0, 102, 153));
        tabel.getTableHeader().setForeground(Color.WHITE);
        tabel.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabel.setSelectionBackground(new Color(173, 216, 230));

        // Set lebar kolom
        int[] colWidths = {90, 55, 55, 55, 60, 55, 50};
        for (int i = 0; i < colWidths.length; i++) {
            tabel.getColumnModel().getColumn(i).setPreferredWidth(colWidths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(tabel);
        scrollPane.setPreferredSize(new Dimension(460, 180));
        panelTabel.add(scrollPane, BorderLayout.CENTER);

        add(panelForm, BorderLayout.NORTH);
        add(panelTabel, BorderLayout.CENTER);

        pack();
        setSize(480, 540);

        // ===== ACTION LISTENERS =====
        btnInput.addActionListener(e -> inputData());
        btnClear.addActionListener(e -> clearForm());
        btnClose.addActionListener(e -> dispose());
    }

    private void inputData() {
        // Cek kapasitas
        if (jumlahData >= MAX_DATA) {
            JOptionPane.showMessageDialog(this,
                "Data sudah penuh! Maksimal " + MAX_DATA + " data.",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validasi input
        String nim = txtNIM.getText().trim();
        if (nim.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIM tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtNIM.requestFocus();
            return;
        }

        // Cek NIM duplikat
        for (int i = 0; i < jumlahData; i++) {
            if (arrNIM[i].equals(nim)) {
                JOptionPane.showMessageDialog(this, "NIM sudah diinput sebelumnya!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        double uts, tugas, uas, absensi;
        try {
            uts     = Double.parseDouble(txtUTS.getText().trim());
            tugas   = Double.parseDouble(txtTugas.getText().trim());
            uas     = Double.parseDouble(txtUAS.getText().trim());
            absensi = Double.parseDouble(txtAbsensi.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Nilai harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validasi range 0-100
        if (!isValidRange(uts) || !isValidRange(tugas) || !isValidRange(uas) || !isValidRange(absensi)) {
            JOptionPane.showMessageDialog(this, "Nilai harus antara 0 - 100!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Hitung nilai akhir: UTS*30% + Tugas*20% + UAS*40% + Absensi*10%
        double akhir = (uts * 0.30) + (tugas * 0.20) + (uas * 0.40) + (absensi * 0.10);
        String grade = hitungGrade(akhir);

        // Simpan ke array
        arrNIM[jumlahData]     = nim;
        arrUTS[jumlahData]     = uts;
        arrTugas[jumlahData]   = tugas;
        arrUAS[jumlahData]     = uas;
        arrAbsensi[jumlahData] = absensi;
        arrAkhir[jumlahData]   = akhir;
        jumlahData++;

        // Tambah ke tabel
        modelTabel.addRow(new Object[]{
            nim,
            String.format("%.1f", uts),
            String.format("%.1f", tugas),
            String.format("%.1f", uas),
            String.format("%.1f", absensi),
            String.format("%.2f", akhir),
            grade
        });

        // Update status
        lblStatus.setText("Data ke-" + jumlahData + " berhasil diinput. Sisa: " + (MAX_DATA - jumlahData));
        clearForm();

        if (jumlahData == MAX_DATA) {
            lblStatus.setText("Data penuh! (" + MAX_DATA + "/" + MAX_DATA + ") - Tampilkan rekapitulasi");
            btnInput.setEnabled(false);
            tampilkanRekap();
        }
    }

    private String hitungGrade(double nilai) {
        if (nilai >= 80) return "A";
        else if (nilai >= 70) return "B";
        else if (nilai >= 60) return "C";
        else if (nilai >= 50) return "D";
        else return "E";
    }

    private boolean isValidRange(double nilai) {
        return nilai >= 0 && nilai <= 100;
    }

    private void tampilkanRekap() {
        double totalAkhir = 0;
        double maxAkhir   = arrAkhir[0];
        double minAkhir   = arrAkhir[0];
        String nimTerbaik = arrNIM[0];
        String nimTerendah= arrNIM[0];

        for (int i = 0; i < jumlahData; i++) {
            totalAkhir += arrAkhir[i];
            if (arrAkhir[i] > maxAkhir) { maxAkhir = arrAkhir[i]; nimTerbaik  = arrNIM[i]; }
            if (arrAkhir[i] < minAkhir) { minAkhir = arrAkhir[i]; nimTerendah = arrNIM[i]; }
        }
        double rataRata = totalAkhir / jumlahData;

        String pesan = String.format(
            "====== REKAPITULASI NILAI ======\n" +
            "Jumlah Mahasiswa : %d\n" +
            "Rata-rata Nilai  : %.2f (%s)\n" +
            "Nilai Tertinggi  : %.2f (NIM: %s)\n" +
            "Nilai Terendah   : %.2f (NIM: %s)\n",
            jumlahData, rataRata, hitungGrade(rataRata),
            maxAkhir, nimTerbaik, minAkhir, nimTerendah);

        JOptionPane.showMessageDialog(this, pesan, "Rekapitulasi", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearForm() {
        txtNIM.setText("");
        txtUTS.setText("");
        txtTugas.setText("");
        txtUAS.setText("");
        txtAbsensi.setText("");
        txtNIM.requestFocus();
    }
}
