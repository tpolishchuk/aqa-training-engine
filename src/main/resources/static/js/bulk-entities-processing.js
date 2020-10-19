$(document).ready(function() {

    $("#form-generate-random-tasks").submit(function(event) {

              event.preventDefault();
              $("#submit-generate-random-tasks-btn").attr("disabled", true);

              var action_url = $(this).attr("action");
              var request_method = "POST";
              var form_data = $(this).serialize();

              $.ajax({
                type : request_method,
                url : window.origin + action_url,
                data : form_data,
                success: function(){
                  $("#generate-random-tasks-result-message")
                                .html("<p class='alert alert-success form-alert'>All requested tasks are successfully generated</p>");
                  $("#submit-generate-random-tasks-btn").attr("disabled", false);
                  $('#generate-random-tasks-result-message').delay(1500).fadeOut('slow');
                },
                error : function(e) {
                  console.log("User tasks creation error: ", e);
                  $("#generate-random-tasks-result-message")
                                .html("<p class='alert alert-danger form-alert'>Unable to create requested tasks</p>");
                  $("#generate-random-tasks-result-message").focus();
                }
              });
            });

    $("#form-generate-random-notes").submit(function(event) {

              event.preventDefault();
              $("#submit-generate-random-notes-btn").attr("disabled", true);

              var action_url = $(this).attr("action");
              var request_method = "POST";
              var form_data = $(this).serialize();

              $.ajax({
                type : request_method,
                url : window.origin + action_url,
                data : form_data,
                success: function(){
                  $("#generate-random-notes-result-message")
                                .html("<p class='alert alert-success form-alert'>All requested notes are successfully generated</p>");
                  $("#submit-generate-random-notes-btn").attr("disabled", false);
                  $('#generate-random-notes-result-message').delay(1500).fadeOut('slow');
                },
                error : function(e) {
                  console.log("User notes creation error: ", e);
                  $("#generate-random-notes-result-message")
                                .html("<p class='alert alert-danger form-alert'>Unable to create requested notes</p>");
                  $("#generate-random-notes-result-message").focus();
                }
              });
            });

    $("#form-delete-all-user-tasks").submit(function(event) {

          event.preventDefault();
          $("#submit-delete-all-user-tasks").attr("disabled", true);

          var action_url = $(this).attr("action");
          var request_method = "DELETE";
          var form_data = $(this).serialize();

          $.ajax({
            type : request_method,
            url : window.origin + action_url,
            data : form_data,
            success: function(){
              $("#delete-all-user-tasks-result-message")
                            .html("<p class='alert alert-success form-alert'>All tasks are successfully deleted</p>");
              $("#submit-delete-all-user-tasks").attr("disabled", false);
              $('#delete-all-user-tasks-result-message').delay(1500).fadeOut('slow');
            },
            error : function(e) {
              $("#delete-all-user-tasks-result-message")
                            .html("<p class='alert alert-danger form-alert'>Unable to delete all user tasks</p>");
              console.log("User tasks deletion error: ", e);
            }
          });
        });

    $("#form-delete-all-user-notes").submit(function(event) {

          event.preventDefault();
          $("#submit-delete-all-user-notes").attr("disabled", true);

          var action_url = $(this).attr("action");
          var request_method = "DELETE";
          var form_data = $(this).serialize();

          $.ajax({
            type : request_method,
            url : window.origin + action_url,
            data : form_data,
            success: function(){
              $("#delete-all-user-notes-result-message")
                            .html("<p class='alert alert-success form-alert'>All notes are successfully deleted</p>");
              $("#submit-delete-all-user-notes").attr("disabled", false);
              $('#delete-all-user-notes-result-message').delay(1500).fadeOut('slow');
            },
            error : function(e) {
              $("#delete-all-user-notes-result-message")
                            .html("<p class='alert alert-danger form-alert'>Unable to delete all user notes</p>");
              console.log("User notes deletion error: ", e);
            }
          });
        });
})
