 	Feature: Validate DIrectory
 Scenario Outline: Validate directory creation
    Given I created a directory <dirName> in my home directory
    When I write 5 files with 10 lines each in <dirName>
    Then I list all the files in <dirName>
    Then I concatenate all files in <dirName> into a temp file
    Then I download the <fileName> from <dirName>
    Then i print the content in the <fileName> 

    Examples:
    | dirName |fileName |
    | dir1    |temp.txt |