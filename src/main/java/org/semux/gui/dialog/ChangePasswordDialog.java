package org.semux.gui.dialog;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.semux.Kernel;
import org.semux.core.Wallet;
import org.semux.gui.Action;

public class ChangePasswordDialog extends JDialog implements ActionListener {

    public ChangePasswordDialog(Frame owner) {
        super(owner);
        this.setMinimumSize(new Dimension(400, 240));
        this.setLocationRelativeTo(owner);
        this.setTitle("Change password");

        JLabel lblOldPassword = new JLabel("Old Password:");

        JLabel lblPassword = new JLabel("Password:");

        JLabel lblRepeat = new JLabel("Repeat Password:");

        oldpasswordField = new JPasswordField();

        passwordField = new JPasswordField();

        repeatField = new JPasswordField();

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(this);
        btnOk.setActionCommand(Action.OK.name());

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(this);
        btnCancel.setActionCommand(Action.CANCEL.name());

        // @formatter:off
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGap(20)
                    .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addComponent(lblPassword)
                        .addComponent(lblOldPassword)
                        .addComponent(lblRepeat))
                    .addGap(18)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(btnOk, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
                            .addGap(18)
                            .addComponent(btnCancel))
                        .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                            .addComponent(repeatField, GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                            .addComponent(passwordField, GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                            .addComponent(oldpasswordField, GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)))
                    .addGap(23))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGap(32)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblOldPassword)
                        .addComponent(oldpasswordField, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                    .addGap(18)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblPassword)
                        .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                    .addGap(18)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblRepeat)
                        .addComponent(repeatField, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                    .addGap(18)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnOk)
                        .addComponent(btnCancel))
                    .addContainerGap(77, Short.MAX_VALUE))
        );
        getContentPane().setLayout(groupLayout);
        // @formatter:on
    }

    private static final long serialVersionUID = 1L;
    private JPasswordField oldpasswordField;
    private JPasswordField passwordField;
    private JPasswordField repeatField;

    @Override
    public void actionPerformed(ActionEvent e) {
        Action action = Action.valueOf(e.getActionCommand());

        switch (action) {
        case OK: {
            String oldPassword = new String(oldpasswordField.getPassword());
            String password = new String(passwordField.getPassword());
            String repeat = new String(repeatField.getPassword());

            Kernel kernel = Kernel.getInstance();
            Wallet wallet = kernel.getWallet();

            if (!password.equals(repeat)) {
                JOptionPane.showMessageDialog(this, "Repeat password does not match!");
            } else if (!wallet.getPassword().equals(oldPassword)) {
                JOptionPane.showMessageDialog(this, "Incorrect password!");
            } else {
                wallet.changePassword(password);
                wallet.flush();
                JOptionPane.showMessageDialog(this, "Password changed!");
                this.dispose();
            }
            break;
        }
        case CANCEL: {
            this.dispose();
            break;
        }
        default:
            break;
        }
    }
}
