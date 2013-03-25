Ext.define('Admin.controller.account.GroupWizardController', {
    extend: 'Admin.controller.account.GroupController',

    stores: [],
    models: [],
    views: [],

    init: function () {
        var me = this;

        this.control({
            'groupWizardPanel *[action=newGroup]': {
                click: this.createNewGroup
            },
            'groupWizardPanel *[action=closeWizard]': {
                click: this.closeWizard
            },
            'groupWizardPanel *[action=saveGroup]': {
                click: function (el, e) {
                    var groupWizard = el.up('groupWizardPanel');
                    this.saveGroup(groupWizard, false);
                }
            },
            'groupWizardPanel *[action=deleteGroup]': {
                click: this.deleteGroup
            },
            'groupWizardPanel *[displayNameSource]': {
                change: this.onDisplayNameSourceChanged
            },
            'groupWizardPanel': {
                afterrender: this.onGroupWizardLoaded
            },
            'groupWizardPanel wizardHeader': {
                displaynamechange: this.onDisplayNameChanged,
                displaynameoverride: this.onDisplayNameOverriden
            },
            'groupWizardPanel': {
                beforestepchanged: this.validateStep,
                stepchanged: this.stepChanged,
                finished: function (wizard, data) {
                    var groupWizard = wizard.up('groupWizardPanel');
                    this.saveGroup(groupWizard, true);
                },
                validitychange: this.validityChanged,
                dirtychange: this.dirtyChanged
            }
        });

        this.application.on({
            groupWizardNext: {
                fn: this.wizardNext,
                scope: this
            },
            groupWizardPrev: {
                fn: this.wizardPrev,
                scope: this
            }
        });
    },

    onDisplayNameChanged: function (newName, oldName) {
        this.getTopBar().setTitleButtonText(newName);
    },

    onDisplayNameOverriden: function (overriden) {
        this.getGroupWizardPanel().autogenerateDisplayName = !overriden;
    },

    onDisplayNameSourceChanged: function (field, event, opts) {
        var wizard = this.getGroupWizardPanel();
        if (wizard.autogenerateDisplayName) {
            var displayName = this.autoGenerateDisplayName();
            wizard.getWizardHeader().setDisplayName(displayName);
        }
    },

    autoGenerateDisplayName: function () {
        var displayName = '';
        var activeItem = this.getGroupWizardPanel().getActiveItem();
        var fields = activeItem.query('*[displayNameSource]');
        if (fields.length > 0) {
            var eachFn = function (item, index, all) {
                displayName += item.getValue() + " ";
            };
            Ext.Array.forEach(fields, eachFn);
        }
        return Ext.String.trim(displayName).toLowerCase();
    },

    onGroupWizardLoaded: function (wizard) {
        // in case of edit check if the auto generated name
        // is not equal to the actual meaning it was user overriden
        if (!wizard.isNewGroup()) {
            var displayNameValue = (wizard.getWizardHeader().getDisplayName() || "").toLowerCase();
            var generatedDisplayName = this.autoGenerateDisplayName();
            wizard.autogenerateDisplayName = generatedDisplayName === displayNameValue;
        }
    },

    saveGroup: function (groupWizard, closeWizard) {
        var me = this;
        var data = groupWizard.getData();
        data.name = data.displayName;
        var step = groupWizard.wizard.getLayout().getActiveItem();
        if (Ext.isFunction(step.getData)) {
            Ext.merge(data, step.getData());
        }

        var onUpdateGroupSuccess = function (key) {
            groupWizard.addData({
                'key': key
            });
            if (closeWizard) {
                me.getGroupWizardTab().close();
            }

            var isNewGroup = groupWizard.isNewGroup();
            var feedbackTitle = isNewGroup ? 'Group was created' : 'Group was updated';
            Admin.MessageBus.showFeedback({
                title: feedbackTitle,
                message: 'Something just happened! Li Europan lingues es membres del sam familie. Lor separat existentie es un myth.',
                opts: {}
            });

            var current = me.getAccountGridPanel().store.currentPage;
            me.getAccountGridPanel().store.loadPage(current);
        };
        this.remoteCreateOrUpdateGroup(data, onUpdateGroupSuccess);
    },


    deleteGroup: function (el, e) {
        var group = this.getGroupWizardTab().data;
        if (group) {
            this.showDeleteAccountWindow(group);
        }
    },

    validateStep: function (wizard, step) {
        var data;
        if (step.getData) {
            data = step.getData();
        }
        if (data) {
            wizard.addData(data);
        }
        return true;
    },

    stepChanged: function (groupWizard, oldStep, newStep) {
        if (newStep.getXType() === 'summaryTreePanel') {
            var treePanel = newStep;

            treePanel.setDiffData(groupWizard.modelData, groupWizard.getData());
        }
    },

    validityChanged: function (wizard, valid) {
        // Need to go this way up the hierarchy in case there are multiple wizards
        var tb = wizard.down('groupWizardToolbar');
        var pb = wizard.getProgressBar();
        var save = tb.down('#save');
        //var finish = wizard.down('#finish');
        var conditionsMet = valid && (wizard.isWizardDirty || wizard.isNew);
        save.setDisabled(!conditionsMet);
        //finish.setVisible(conditionsMet);
        pb.setDisabled(wizard.isNew ? !wizard.isStepValid() : !conditionsMet);
    },

    dirtyChanged: function (wizard, dirty) {
        var tb = wizard.down('groupWizardToolbar');
        var pb = wizard.getProgressBar();
        var save = tb.down('#save');
        //var finish = wizard.down('#controls #finish');
        var conditionsMet = (dirty || wizard.isNew) && wizard.isWizardValid;
        save.setDisabled(!conditionsMet);
        //finish.setVisible(conditionsMet);
        pb.setDisabled(wizard.isNew ? !wizard.isStepValid() : !conditionsMet);
    },

    wizardPrev: function (btn, evt) {
        var wizard = this.getGroupWizardPanel().getWizardPanel();
        wizard.prev(btn);
    },

    wizardNext: function (btn, evt) {
        var wizard = this.getGroupWizardPanel().getWizardPanel();
        if (wizard.isStepValid()) {
            wizard.next(btn);
        }
    },

    createNewGroup: function (el, e) {
        this.showNewAccountWindow('group');
    },

    closeWizard: function (el, e) {
        var tab = this.getGroupWizardTab();
        var wizard = tab.down('wizardPanel');
        if (wizard.isWizardDirty) {
            Ext.Msg.confirm('Close wizard', 'There are unsaved changes, do you want to close it anyway ?',
                function (answer) {
                    if ('yes' === answer) {
                        tab.close();
                    }
                });
        } else {
            tab.close();
        }
    },


    /*      Getters     */

    getGroupWizardTab: function () {
        return this.getCmsTabPanel().getActiveTab();
    },

    getGroupWizardPanel: function () {
        return this.getGroupWizardTab().items.get(0);
    }

});
