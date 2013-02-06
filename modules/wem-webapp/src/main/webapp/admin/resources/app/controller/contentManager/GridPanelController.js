Ext.define('Admin.controller.contentManager.GridPanelController', {
    extend: 'Admin.controller.contentManager.Controller',

    /*      Controller for handling Grid & its Context Menu UI events       */

    stores: [
        'Admin.store.contentManager.ContentStore',
        'Admin.store.contentManager.ContentTreeStore'
    ],
    models: [
        'Admin.model.contentManager.ContentModel'
    ],
    views: [
        'Admin.view.contentManager.FilterPanel',
        'Admin.view.contentManager.ShowPanel',
        'Admin.view.contentManager.ContextMenu'
    ],

    init: function () {

        this.control({
            'contentTreeGridPanel treepanel, grid': {
                selectionchange: function (panel, selected, opts) {
                    this.updateDetailPanel(selected);
                    this.updateToolbarButtons(selected);
                },
                itemcontextmenu: this.popupMenu,
                itemdblclick: function (grid, record, el, index, event, opts) {
                    this.editContent(record);
                }
            },
            'contentManagerContextMenu *[action=deleteContent]': {
                click: function (el, e) {
                    this.deleteContent();
                }
            },
            'contentManagerContextMenu *[action=editContent]': {
                click: function (el, e) {
                    this.editContent();
                }
            },
            'contentManagerContextMenu *[action=viewContent]': {
                click: function (el, e) {
                    this.viewContent();
                }
            }
        });
    },

    popupMenu: function (view, rec, node, index, e) {
        e.stopEvent();
        this.getContentManagerContextMenu().showAt(e.getXY());
        return false;
    },


    /*      Getters     */

    getContentManagerContextMenu: function () {
        var menu = Ext.ComponentQuery.query('contentManagerContextMenu')[0];
        if (!menu) {
            menu = Ext.create('widget.contentManagerContextMenu');
        }
        return menu;
    }

});
