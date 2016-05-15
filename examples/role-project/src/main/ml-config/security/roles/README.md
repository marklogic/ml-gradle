To create a role that refers to itself in the permissions array, you'll need to make 2 separate HTTP
requests (just like you would via the admin UI), and thus you'll need 2 separate files. One file defines only
the role name so that the role can be created. The second file defines everything else. The files are
named to guarantee that the first file - the one with only the role name - is processed first. 
