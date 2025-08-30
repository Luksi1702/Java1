-- Create Database
CREATE DATABASE ARTICLES;
GO

USE ARTICLES;
GO

-- Create Tables

CREATE TABLE Person (
    IDPerson INT PRIMARY KEY IDENTITY,
    Name NVARCHAR(90),
    Surname NVARCHAR(90),
    Email NVARCHAR(300)
);
GO

CREATE TABLE Article (
    IDArticle INT PRIMARY KEY IDENTITY,
    Title NVARCHAR(300),
    Link NVARCHAR(300),
    Description NVARCHAR(900),
    PublishedDate NVARCHAR(90),
    Creator INT,
    PicturePath NVARCHAR(90),
    Content NVARCHAR(MAX),
    FOREIGN KEY (Creator) REFERENCES Person(IDPerson) ON DELETE SET NULL
);
GO

CREATE TABLE User_ (
    IDUser INT PRIMARY KEY IDENTITY,
    Username NVARCHAR(50) NOT NULL,
    PwdHash NVARCHAR(256) NOT NULL,
    PwdSalt NVARCHAR(256) NOT NULL,
    IsAdmin INT DEFAULT 0,
    PersonID INT UNIQUE,
    FOREIGN KEY (PersonID) REFERENCES Person(IDPerson) ON DELETE SET NULL
);
GO

CREATE TABLE ArticleContributor (
    IDArticle INT,
    IDPerson INT,
    PRIMARY KEY (IDArticle, IDPerson),
    FOREIGN KEY (IDArticle) REFERENCES Article(IDArticle) ON DELETE CASCADE,
    FOREIGN KEY (IDPerson) REFERENCES Person(IDPerson) ON DELETE CASCADE
);
GO

-- Stored Procedures

-- Create Procedures

CREATE PROCEDURE createArticle
    @Title NVARCHAR(300),
    @Link NVARCHAR(300),
    @Description NVARCHAR(900),
    @PublishedDate NVARCHAR(90),
    @Creator INT,
    @PicturePath NVARCHAR(90),
    @Content NVARCHAR(MAX),
    @IDArticle INT OUTPUT
AS
BEGIN
    INSERT INTO Article (Title, Link, Description, PublishedDate, Creator, PicturePath, Content)
    VALUES (@Title, @Link, @Description, @PublishedDate, @Creator, @PicturePath, @Content);
    
    SET @IDArticle = SCOPE_IDENTITY();
END;
GO

CREATE PROCEDURE createPerson
    @Name NVARCHAR(90),
    @Surname NVARCHAR(90),
    @Email NVARCHAR(300),
    @IDPerson INT OUTPUT
AS
BEGIN
    INSERT INTO Person (Name, Surname, Email)
    VALUES (@Name, @Surname, @Email);
    
    SET @IDPerson = SCOPE_IDENTITY();
END;
GO

CREATE PROCEDURE createUser
    @Username NVARCHAR(50),
    @PwdHash NVARCHAR(256),
    @PwdSalt NVARCHAR(256),
    @IsAdmin INT,
    @PersonID INT,
    @IDUser INT OUTPUT
AS
BEGIN
    IF @IsAdmin IS NULL
        SET @IsAdmin = 0;

    INSERT INTO User_ (Username, PwdHash, PwdSalt, IsAdmin, PersonID)
    VALUES (@Username, @PwdHash, @PwdSalt, @IsAdmin, @PersonID);

    SET @IDUser = SCOPE_IDENTITY();
END;
GO

CREATE PROCEDURE insertArticleContributor
    @IDArticle INT,
    @IDPerson INT
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (SELECT 1 FROM Article WHERE IDArticle = @IDArticle)
       AND EXISTS (SELECT 1 FROM Person WHERE IDPerson = @IDPerson)
    BEGIN
        INSERT INTO ArticleContributor (IDArticle, IDPerson)
        VALUES (@IDArticle, @IDPerson);
    END
    ELSE
    BEGIN
        RAISERROR('Either the Article or Person does not exist.', 16, 1);
    END
END;
GO

-- Update Procedures

CREATE PROCEDURE updateArticle
    @Title NVARCHAR(300),
    @Link NVARCHAR(300),
    @Description NVARCHAR(900),
    @PublishedDate NVARCHAR(90),
    @Creator INT,
    @PicturePath NVARCHAR(90),
    @Content NVARCHAR(MAX),
    @IDArticle INT
AS
BEGIN
    UPDATE Article SET
        Title = @Title,
        Link = @Link,
        Description = @Description,
        PublishedDate = @PublishedDate,
        Creator = @Creator,
        PicturePath = @PicturePath,
        Content = @Content
    WHERE IDArticle = @IDArticle;
END;
GO

CREATE PROCEDURE updatePerson
    @Name NVARCHAR(90),
    @Surname NVARCHAR(90),
    @Email NVARCHAR(300),
    @IDPerson INT
AS
BEGIN
    UPDATE Person SET
        Name = @Name,
        Surname = @Surname,
        Email = @Email
    WHERE IDPerson = @IDPerson;
END;
GO

CREATE PROCEDURE updateUser
    @Username NVARCHAR(50),
    @PwdHash NVARCHAR(256),
    @PwdSalt NVARCHAR(256),
    @IsAdmin INT,
    @PersonID INT,
    @IDUser INT
AS
BEGIN
    UPDATE User_ SET
        Username = @Username,
        PwdHash = @PwdHash,
        PwdSalt = @PwdSalt,
        IsAdmin = @IsAdmin,
        PersonID = @PersonID
    WHERE IDUser = @IDUser;
END;
GO

-- Delete Procedures

CREATE PROCEDURE deleteArticle
    @IDArticle INT
AS
BEGIN
    DELETE FROM Article WHERE IDArticle = @IDArticle;
END;
GO

CREATE PROCEDURE deletePerson
    @IDPerson INT
AS
BEGIN
    DELETE FROM Person WHERE IDPerson = @IDPerson;
END;
GO

CREATE PROCEDURE deleteUser
    @IDUser INT
AS
BEGIN
    DELETE FROM User_ WHERE IDUser = @IDUser;
END;
GO

CREATE PROCEDURE deleteArticleContributor
    @IDArticle INT,
    @IDPerson INT
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (
        SELECT 1 
        FROM ArticleContributor 
        WHERE IDArticle = @IDArticle AND IDPerson = @IDPerson
    )
    BEGIN
        DELETE FROM ArticleContributor 
        WHERE IDArticle = @IDArticle AND IDPerson = @IDPerson;
    END
    ELSE
    BEGIN
        RAISERROR('ArticleContributor entry not found.', 16, 1);
    END
END;
GO


-- Select Procedures

CREATE PROCEDURE selectArticle
    @IDArticle INT
AS
BEGIN
    SELECT * FROM Article WHERE IDArticle = @IDArticle;
END;
GO

CREATE PROCEDURE selectArticles
AS
BEGIN
    SELECT * FROM Article;
END;
GO

CREATE PROCEDURE selectPerson
    @IDPerson INT
AS
BEGIN
    SELECT * FROM Person WHERE IDPerson = @IDPerson;
END;
GO

CREATE PROCEDURE selectPeople
AS
BEGIN
    SELECT * FROM Person;
END;
GO

CREATE PROCEDURE selectUser
    @IDUser INT
AS
BEGIN
    SELECT * FROM User_ WHERE IDUser = @IDUser;
END;
GO

CREATE PROCEDURE selectUsers
AS
BEGIN
    SELECT * FROM User_;
END;
GO

CREATE PROCEDURE getArticleContributors
    @IDArticle INT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        p.IDPerson,
        p.Name,
        p.Surname,
        p.Email
    FROM 
        ArticleContributor ac
    INNER JOIN 
        Person p ON ac.IDPerson = p.IDPerson
    WHERE 
        ac.IDArticle = @IDArticle;
END;
GO


CREATE PROCEDURE checkUser
    @Username NVARCHAR(50),
    @PwdHash NVARCHAR(256),
    @PwdSalt NVARCHAR(256),
    @IDUser INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT @IDUser = IDUser
    FROM User_
    WHERE Username = @Username
      AND PwdHash = @PwdHash
      AND PwdSalt = @PwdSalt;

    IF @IDUser IS NULL
    BEGIN
        SET @IDUser = -1;
    END
END;
GO

CREATE PROCEDURE getSaltByUsername
    @Username NVARCHAR(50),
    @PwdSalt NVARCHAR(256) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT @PwdSalt = PwdSalt
    FROM User_
    WHERE Username = @Username;

    IF @PwdSalt IS NULL
    BEGIN
        SET @PwdSalt = '';
    END
END;
GO

CREATE PROCEDURE checkIfUserIsAdmin
    @IDUser INT,
    @IsAdmin BIT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT @IsAdmin = 
        CASE 
            WHEN IsAdmin = 1 THEN 1
            ELSE 0
        END
    FROM User_
    WHERE IDUser = @IDUser;
END;
GO


/*Generate Admin*/
insert into Person (Name,Surname,Email) values ('Admin', 'Admin', 'admin@algebra.hr')
insert into User_ (Username,PwdHash,PwdSalt,PersonID,IsAdmin) values ('admin','e2as+rFV/OFN2lRetJKIORIFl55iB8utxPgF1N9fBaI=','CW3G5mmRMGSyWKgycCFuSQ==',1,1)
insert into Person (Name,Surname,Email) values ('John', 'Doe', 'jdoe@algebra.hr')

