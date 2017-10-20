var t = require('/lib/xp/testing.js');
var authLib = require('/lib/xp/auth.js');

exports.createUserStore = function () {

    var result = authLib.createUserStore({
        name: 'userStoreTestKey',
        displayName: 'User store test',
        description: 'User store used for testing',
        idProvider: {
            applicationKey: 'com.enonic.app.test',
            config: [{}]
        },
        permissions: [{
            principal: 'role:system.admin',
            access: 'ADMINISTRATOR'
        }]
    });

    var expectedJson = {
        'key': 'userStoreTestKey',
        'displayName': 'User store test',
        'description': 'User store used for testing',
        'authConfig': {
            'applicationKey': 'com.enonic.app.test',
            'config': {
                'set': {
                    'subString': 'subStringValue',
                    'subLong':123
                },
                'string': 'stringValue'
            }
        }
    };

    t.assertJsonEquals(expectedJson, result, 'createUserStore result not equals');

};
