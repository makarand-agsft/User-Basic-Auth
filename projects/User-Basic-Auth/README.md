# User-Basic-Auth

## Application setup on local
Step by step document is availbale on Google drive. Ensure to follow as mentioned in the document. If something does not work then do let us know the problem/solution so that the document can be updated for others to follow. 

[Setup document](https://docs.google.com/document/d/1XezA8VyuoP-2HXdt4fRP4DNz4v7S7lM4_nmJ3udHv0M/edit)

## Branching strategy
Development cannot be done directly into main branch. Each developer to develop the code in specific branch and push the code. Once code is stable it will be merged into main.

Please follow these steps to manage branch -
1. ``git status`` > to ensure no uncommited change exist. If exists then either commit or stash.
2. ``git checkout main`` > switch to main branch
3. ``git pull`` > get the latest main branch code
4. ``git checkout -b branchname`` > create and switches to branch where branchname should be of format ``firstname.lastname``
5. ``git push -u origin branchname`` > push the branch to server where branchname should be replaced with the branch created in earlier step 
6. Do the development in the branch and commit regularly so that changes are not lost due to any problem with your local machine
7. If the development is taking multiple days then ensure to take latest from main or at least before raising the pull request

Please follow these steps to update branch with latest from main -
1. ``git status`` > to ensure no uncommited change exist. If exists then either commit or stash.
2. ``git checkout main`` > switch to main branch
3. ``git pull`` > get the latest main branch code
4. ``git checkout branchname`` > switch to your branch
5. ``git merge main`` > latest main code merged to branch, resolve any conflicts

