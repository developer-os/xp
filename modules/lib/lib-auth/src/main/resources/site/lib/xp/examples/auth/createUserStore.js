var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Creates a user.
var user = authLib.createUserStore({
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
// END

// BEGIN
// Information when creating a user.
var expected = {
    'key': 'userStoreTestKey',
    'displayName': 'User store test',
    'description': 'User store used for testing',
    'authConfig': {
        'applicationKey': 'com.enonic.app.test',
        'config': {
            'set': {
                'subString': 'subStringValue',
                'subLong': 123
            },
            'string': 'stringValue'
        }
    }
};
// END

t.assertJsonEquals(expected, user);
