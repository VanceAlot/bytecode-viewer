package the.bytecode.club.bytecodeviewer.gui.resourceviewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Configuration;
import the.bytecode.club.bytecodeviewer.gui.components.VisibleComponent;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ClassViewer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.FileViewer;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ResourceViewer;
import the.bytecode.club.bytecodeviewer.util.FileContainer;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import static the.bytecode.club.bytecodeviewer.Constants.BLOCK_TAB_MENU;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * The pane that contains all of the resources as tabs.
 *
 * @author Konloch
 * @author WaterWolf
 */
public class WorkPaneMainComponent extends VisibleComponent
{
    public final JTabbedPane tabs;
    public final JPanel buttonPanel;
    public final JButton refreshClass;
    public final HashMap<String, Integer> workingOn = new HashMap<>();

    public WorkPaneMainComponent()
    {
        super("WorkPanel");
        setTitle("Work Space");

        this.tabs = new JTabbedPane();
        
        JPopupMenu popUp = new JPopupMenu();
        JMenuItem closeAllTabs = new JMenuItem("Close All But This");
        JMenuItem closeTab = new JMenuItem("Close Tab");
        closeTab.addActionListener(e ->
        {
            TabExitButton tabExitButton = (TabExitButton) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
            final int index = tabExitButton.getTabIndex();
            
            if (index != -1)
                tabs.remove(index);
        });
        closeAllTabs.addActionListener(e ->
        {
            TabExitButton tabExitButton = (TabExitButton) ((JPopupMenu)((JMenuItem) e.getSource()).getParent()).getInvoker();
            final int index = tabExitButton.getTabIndex();
    
            while (true)
            {
                if (tabs.getTabCount() <= 1)
                    return;
        
                if (index != 0)
                    tabs.remove(0);
                else
                    tabs.remove(1);
            }
        });
        tabs.addMouseListener(new MouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e) { }
            @Override
            public void mouseEntered(MouseEvent arg0) { }
            @Override
            public void mouseExited(MouseEvent arg0) { }

            @Override
            public void mousePressed(MouseEvent e)
            {
                if (BLOCK_TAB_MENU)
                    return;
                
                if (e.getButton() == 3)
                {
                    Rectangle bounds = new Rectangle(1, 1, e.getX(), e.getY());
                    
                    for (int i = 0; i < BytecodeViewer.viewer.workPane.tabs.getTabCount(); i++)
                    {
                        Component c = BytecodeViewer.viewer.workPane.tabs.getTabComponentAt(i);
                        if (c != null && bounds.intersects(c.getBounds()))
                        {
                            popUp.setVisible(true);
                            closeAllTabs.setText("Close All But This: " + ((TabbedPane) c).tabName);
                            closeTab.setText("Close Tab: " + ((TabbedPane) c).tabName);
                        }
                        else
                        {
                            popUp.setVisible(false);
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) { }
        });

        popUp.add(closeAllTabs);
        popUp.add(closeTab);
        
        if (!BLOCK_TAB_MENU)
            tabs.setComponentPopupMenu(popUp);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabs, BorderLayout.CENTER);

        buttonPanel = new JPanel(new FlowLayout());

        refreshClass = new JButton("Refresh");
        refreshClass.addActionListener((event)->{
            Thread t = new Thread(() -> new WorkPaneRefresh(event).run());
            t.start();
        });

        buttonPanel.add(refreshClass);

        buttonPanel.setVisible(false);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        tabs.addContainerListener(new TabRemovalEvent());
        tabs.addChangeListener(arg0 -> buttonPanel.setVisible(tabs.getSelectedIndex() != -1));

        this.setVisible(true);
    }

    public void addWorkingFile(final FileContainer container, String name, final ClassNode cn)
    {
        String workingName = container.name + ">" + name;
        
        if (Configuration.simplifiedTabNames)
            name = MiscUtils.getChildFromPath(name);
        if (Configuration.displayParentInTab)
            name = container.name + ">" + name;

        if (!workingOn.containsKey(workingName))
        {
            final ClassViewer tabComp = new ClassViewer(container, name, cn, workingName);
            tabs.add(tabComp);
            final int tabIndex = tabs.indexOfComponent(tabComp);
            workingOn.put(workingName, tabIndex);
            
            TabbedPane tabbedPane = new TabbedPane(tabIndex, workingName, container.name, name, tabs);
            tabComp.tabbedPane = tabbedPane;
            tabs.setTabComponentAt(tabIndex, tabbedPane);
            tabs.setSelectedIndex(tabIndex);
        }
        else
        {
            tabs.setSelectedIndex(workingOn.get(workingName));
        }
    }

    public void addFile(final FileContainer container, String name, byte[] contents)
    {
        if (contents == null) //a directory
            return;
        
        final String workingName = container.name + ">" + name;

        if (Configuration.simplifiedTabNames)
            name = MiscUtils.getChildFromPath(name);
        if (Configuration.displayParentInTab)
            name = container.name + ">" + name;

        if (!workingOn.containsKey(workingName))
        {
            final FileViewer tabComp = new FileViewer(container, name, contents, workingName);
            tabs.add(tabComp);
            final int tabIndex = tabs.indexOfComponent(tabComp);
            workingOn.put(workingName, tabIndex);

            TabbedPane tabbedPane = new TabbedPane(tabIndex, workingName, container.name, name, tabs);
            tabComp.tabbedPane = tabbedPane;
            tabs.setTabComponentAt(tabIndex, tabbedPane);
            tabs.setSelectedIndex(tabIndex);
        }
        else
        {
            try {
                tabs.setSelectedIndex(workingOn.get(workingName));
            } catch (Exception e) {
                //workingOn.remove(workingName);
                e.printStackTrace();
            }
        }
    }

    public ResourceViewer getCurrentViewer() {
        return (ResourceViewer) tabs.getSelectedComponent();
    }

    public Component[] getLoadedViewers() {
        return tabs.getComponents();
    }

    public void resetWorkspace()
    {
        tabs.removeAll();
        tabs.updateUI();
    }
    
    private static final long serialVersionUID = 6542337997679487946L;
}